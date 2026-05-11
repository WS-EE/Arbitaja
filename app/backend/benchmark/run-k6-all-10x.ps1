param(
    [string]$LegacyUrl         = "http://localhost:9090",
    [string]$HexUrl            = "http://localhost:8080",
    [string]$Duration          = "30s",
    [int]   $Vus               = 10,
    [string]$ApiKey            = "default_api_key",
    [string]$Username          = "admin",
    [string]$Password          = "admin",
    [string]$UserId            = "1",
    [int]   $NumCompetitors    = 10,
    [int]   $NumCriteria       = 4,
    [int]   $EventsPerCriteria = 50,
    [int]   $Runs              = 10,
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
if (-not $cookie) { throw "Login failed — JSESSIONID not returned. Check credentials." }
$script:Session = $ws
Write-Host "  Logged in (JSESSIONID=$($cookie.Value.Substring(0,8))...)"

# ── create competition ────────────────────────────────────────────────────────
Write-Host "Creating test competition..."
$nowMs = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$futMs = [DateTimeOffset]::UtcNow.AddYears(1).ToUnixTimeMilliseconds()
$competition = Invoke-Api "POST" "$HexUrl/v2/competition" @{
    name           = "AllBench-$timestamp"
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
        description      = "All-bench criterion $c"
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
    $alias = "ab_${ts2}_$i"
    $comp  = Invoke-Api "POST" "$HexUrl/v2/competitor" @{
        alias                    = $alias
        public_display_name_type = 1
        full_name                = "AllBench $alias"
        email                    = "${alias}@ab.test"
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
Write-Host ""

# ── scenario catalogue ────────────────────────────────────────────────────────
$scoringExtra     = @("-e","API_KEY=$ApiKey","-e","COMPETITION_ID=$competitionId","-e","COMPETITOR_IDS=$competitorIdsStr","-e","CRITERIA_IDS=$criteriaIdsStr")
$competitionExtra = @("-e","COMPETITION_ID=$competitionId")

$catalogue = @(
    # Scoring (4 scenarios)
    @{ Script="benchmark/k6/scoring-compare.js";     Scenario="dashboard-history";             Group="scoring";     Extra=$scoringExtra }
    @{ Script="benchmark/k6/scoring-compare.js";     Scenario="dashboard-criteria";            Group="scoring";     Extra=$scoringExtra }
    @{ Script="benchmark/k6/scoring-compare.js";     Scenario="dashboard-criteria-competitor"; Group="scoring";     Extra=$scoringExtra }
    @{ Script="benchmark/k6/scoring-compare.js";     Scenario="add-scoring";                  Group="scoring";     Extra=$scoringExtra }
    # PAM (3 scenarios) — RUN_ID is injected per-invocation in the run loop
    @{ Script="benchmark/k6/pam-compare.js";         Scenario="signup";                        Group="pam";         Extra=@("-e","USER_ID=$UserId") }
    @{ Script="benchmark/k6/pam-compare.js";         Scenario="list-users";                    Group="pam";         Extra=@("-e","USER_ID=$UserId") }
    @{ Script="benchmark/k6/pam-compare.js";         Scenario="user-profile";                  Group="pam";         Extra=@("-e","USER_ID=$UserId") }
    # Competition (2 scenarios)
    @{ Script="benchmark/k6/competition-compare.js"; Scenario="list-competitions";              Group="competition"; Extra=$competitionExtra }
    @{ Script="benchmark/k6/competition-compare.js"; Scenario="competitors-in-competition";     Group="competition"; Extra=$competitionExtra }
)
$modes = @("legacy", "hex")

$commonArgs = @(
    "-e", "DURATION=$Duration",
    "-e", "VUS=$Vus",
    "-e", "USERNAME=$Username",
    "-e", "PASSWORD=$Password"
)

# ── accumulate per-run snapshots ──────────────────────────────────────────────
$rawData = @{}
foreach ($entry in $catalogue) {
    $s = $entry.Scenario
    $rawData[$s] = @{ legacy=[System.Collections.Generic.List[pscustomobject]]::new()
                      hex   =[System.Collections.Generic.List[pscustomobject]]::new() }
}

$totalRuns = $Runs * $catalogue.Count * $modes.Count
$done      = 0
$startTime = Get-Date

for ($run = 1; $run -le $Runs; $run++) {
    Write-Host ""
    Write-Host "========== RUN $run / $Runs  ($(Get-Date -Format 'HH:mm:ss')) =========="

    foreach ($entry in $catalogue) {
        foreach ($mode in $modes) {
            $scenario = $entry.Scenario
            $label    = "$scenario-$mode"
            $file     = Join-Path $OutputDir "r${run}-${label}-${timestamp}.json"
            $baseUrl  = if ($mode -eq 'legacy') { $LegacyUrl } else { $HexUrl }

            $done++
            $elapsed = (Get-Date) - $startTime
            $rate    = if ($done -gt 1) { $elapsed.TotalSeconds / ($done - 1) } else { 38 }
            $etaSec  = [int](($totalRuns - $done + 1) * $rate)
            $eta     = (Get-Date).AddSeconds($etaSec).ToString('HH:mm:ss')
            Write-Host "  [$done/$totalRuns ETA $eta]  $label"

            $runId = "${timestamp}-r${run}"
            k6 run $entry.Script --summary-export $file --no-color --quiet `
               @commonArgs @($entry.Extra) `
               -e BASE_URL=$baseUrl -e MODE=$mode -e SCENARIO=$scenario -e RUN_ID=$runId

            $data = Get-Content $file | ConvertFrom-Json
            $rawData[$scenario][$mode].Add([pscustomobject]@{
                requests   = $data.metrics.http_reqs.count
                failedRate = $data.metrics.http_req_failed.value
                p95Ms      = $data.metrics.http_req_duration."p(95)"
                medianMs   = $data.metrics.http_req_duration.med
                avgMs      = $data.metrics.http_req_duration.avg
            })
        }
    }
}

# ── compute averages ──────────────────────────────────────────────────────────
Write-Host ""
Write-Host "========== AVERAGES over $Runs runs =========="
$avgRows = [System.Collections.Generic.List[pscustomobject]]::new()

foreach ($entry in $catalogue) {
    $scenario = $entry.Scenario
    $group    = $entry.Group
    foreach ($mode in $modes) {
        $list = $rawData[$scenario][$mode]
        $avgRows.Add([pscustomobject]@{
            group      = $group
            scenario   = $scenario
            mode       = $mode
            runs       = $list.Count
            requests   = [math]::Round(($list | Measure-Object -Property requests   -Average).Average, 0)
            failedRate = [math]::Round(($list | Measure-Object -Property failedRate -Average).Average, 4)
            p95Ms      = [math]::Round(($list | Measure-Object -Property p95Ms      -Average).Average, 2)
            medianMs   = [math]::Round(($list | Measure-Object -Property medianMs   -Average).Average, 2)
            avgMs      = [math]::Round(($list | Measure-Object -Property avgMs      -Average).Average, 2)
        })
    }
}

$avgRows | Format-Table -AutoSize

$avgCsv = Join-Path $OutputDir "all-avg-${Runs}x-${timestamp}.csv"
$avgRows | Export-Csv -Path $avgCsv -NoTypeInformation
Write-Host ""
Write-Host "Done. Averages saved to: $avgCsv"
Write-Host "Total elapsed: $([math]::Round(((Get-Date)-$startTime).TotalMinutes, 1)) min"
