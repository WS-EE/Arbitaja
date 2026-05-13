param(
    [string]$LegacyUrl         = "http://localhost:9090",
    [string]$HexUrl            = "http://localhost:8080",
    [string]$Duration          = "30s",
    [int]   $Vus               = 10,
    [string]$ApiKey            = "default_api_key",
    [string]$Username          = "admin",
    [string]$Password          = "admin",
    [int]   $NumCompetitors    = 10,
    [int]   $NumCriteria       = 4,
    [int]   $EventsPerCriteria = 50,
    [string]$OutputDir         = "benchmark/results"
)

$ErrorActionPreference = "Stop"
$scriptDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path
Set-Location $projectRoot
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"

# ── helpers ───────────────────────────────────────────────────────────────────
function Invoke-Api([string]$Method, [string]$Url, $Body = $null) {
    $params = @{ Method = $Method; Uri = $Url; WebSession = $script:Session }
    if ($Body) {
        $params.Body        = ($Body | ConvertTo-Json -Compress)
        $params.ContentType = "application/json"
    }
    return Invoke-RestMethod @params
}

# ── login ─────────────────────────────────────────────────────────────────────
Write-Host "Logging in as $Username..."
$ws = New-Object Microsoft.PowerShell.Commands.WebRequestSession
try {
    Invoke-WebRequest -Uri "$HexUrl/login-user" -Method Post `
        -Body "username=$Username&password=$Password" `
        -ContentType "application/x-www-form-urlencoded" `
        -WebSession $ws -MaximumRedirection 0 | Out-Null
} catch {}
$cookie = $ws.Cookies.GetCookies($HexUrl) | Where-Object Name -eq "JSESSIONID"
if (-not $cookie) { throw "Login failed - JSESSIONID not returned. Check credentials." }
$script:Session = $ws
Write-Host "  Logged in (JSESSIONID=$($cookie.Value.Substring(0,8))...)"

# ── create competition ────────────────────────────────────────────────────────
Write-Host "Creating test competition..."
$nowMs = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$futMs = [DateTimeOffset]::UtcNow.AddYears(1).ToUnixTimeMilliseconds()
$competition = Invoke-Api "POST" "$HexUrl/v2/competition" @{
    name           = "ScoreCompare-$timestamp"
    start_time     = $nowMs
    end_time       = $futMs
    score_showtime = $futMs
    publish_scores = $true
    organizer_id   = 1
}
$competitionId = $competition.id
Write-Host "  Created competition id=$competitionId"

# ── create criteria ───────────────────────────────────────────────────────────
Write-Host "Creating $NumCriteria scoring criteria..."
$criteriaIdList = [System.Collections.Generic.List[int]]::new()
for ($c = 1; $c -le $NumCriteria; $c++) {
    $crit = Invoke-Api "POST" "$HexUrl/v2/scoring/criteria" @{
        name             = "Criterion $c"
        description      = "Score compare criterion $c"
        is_manual        = $true
        total_points     = 50.0
        is_generalized   = $true
        visibility_level = 0
        competition_id   = $competitionId
    }
    $criteriaIdList.Add($crit.id)
    Write-Host "  Created criterion id=$($crit.id)"
}

# ── seed competitors with scoring history ─────────────────────────────────────
Write-Host "Seeding $NumCompetitors competitors ($EventsPerCriteria events x $NumCriteria criteria each)..."
$competitorIdList = [System.Collections.Generic.List[int]]::new()
$ts2 = Get-Date -Format "HHmmssff"

for ($i = 1; $i -le $NumCompetitors; $i++) {
    $alias = "sc_${ts2}_$i"
    $comp  = Invoke-Api "POST" "$HexUrl/v2/competitor" @{
        alias                    = $alias
        public_display_name_type = 1
        full_name                = "ScoreCompare $alias"
        email                    = "${alias}@sc.test"
    }
    $cid = $comp.id
    $competitorIdList.Add($cid)
    Invoke-Api "POST" "$HexUrl/v2/competition/$competitionId/competitors/$cid" | Out-Null

    foreach ($criterionId in $criteriaIdList) {
        for ($e = 1; $e -le $EventsPerCriteria; $e++) {
            $pts = [math]::Round((Get-Random -Minimum 0 -Maximum 500) / 10.0, 1)
            Invoke-RestMethod -Method Post -Uri "$HexUrl/v2/scoring/history" `
                -Body (([ordered]@{
                    competition_id = $competitionId
                    competitor_id  = $cid
                    criteria_id    = $criterionId
                    points         = $pts
                }) | ConvertTo-Json -Compress) `
                -ContentType "application/json" `
                -Headers @{ "X-API-KEY" = $ApiKey } | Out-Null
        }
    }
    Write-Host "  Seeded competitor $cid ($alias): $($criteriaIdList.Count * $EventsPerCriteria) events"
}

$competitorIdsStr = $competitorIdList -join ","
$criteriaIdsStr   = $criteriaIdList   -join ","
Write-Host ""
Write-Host "Competition : $competitionId"
Write-Host "Competitors : $competitorIdsStr"
Write-Host "Criteria    : $criteriaIdsStr"

# ── run k6 scenarios ──────────────────────────────────────────────────────────
$scriptPath = "benchmark/k6/scoring-compare.js"
$scenarios  = @("dashboard-history", "dashboard-criteria", "dashboard-criteria-competitor", "add-scoring")
$modes      = @("legacy", "hex")

$commonArgs = @(
    "-e", "DURATION=$Duration",
    "-e", "VUS=$Vus",
    "-e", "API_KEY=$ApiKey",
    "-e", "USERNAME=$Username",
    "-e", "PASSWORD=$Password",
    "-e", "COMPETITION_ID=$competitionId",
    "-e", "COMPETITOR_IDS=$competitorIdsStr",
    "-e", "CRITERIA_IDS=$criteriaIdsStr"
)

$summaries = @{}

foreach ($scenario in $scenarios) {
    foreach ($mode in $modes) {
        $label       = "$scenario-$mode"
        $summaryFile = Join-Path $OutputDir "$label-$timestamp.json"
        $summaries[$label] = $summaryFile
        $baseUrl     = if ($mode -eq 'legacy') { $LegacyUrl } else { $HexUrl }

        Write-Host "Running $label ..."
        k6 run $scriptPath --summary-export $summaryFile @commonArgs `
            -e BASE_URL=$baseUrl -e MODE=$mode -e SCENARIO=$scenario
    }
}

# ── build comparison CSV ──────────────────────────────────────────────────────
$rows = [System.Collections.Generic.List[pscustomobject]]::new()
foreach ($scenario in $scenarios) {
    foreach ($mode in $modes) {
        $label = "$scenario-$mode"
        $data  = Get-Content $summaries[$label] | ConvertFrom-Json
        $rows.Add([pscustomobject]@{
            scenario   = $scenario
            mode       = $mode
            requests   = $data.metrics.http_reqs.count
            failedRate = $data.metrics.http_req_failed.value
            p95Ms      = $data.metrics.http_req_duration."p(95)"
            medianMs   = $data.metrics.http_req_duration.med
            avgMs      = $data.metrics.http_req_duration.avg
        })
    }
}

$comparisonCsv = Join-Path $OutputDir "scoring-comparison-$timestamp.csv"
$rows | Export-Csv -Path $comparisonCsv -NoTypeInformation

Write-Host ""
Write-Host "Results written to $OutputDir"
Write-Host "Comparison CSV: $comparisonCsv"
