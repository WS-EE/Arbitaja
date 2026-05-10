param(
    [string]$LegacyUrl   = "http://localhost:9090",
    [string]$HexUrl      = "http://localhost:8080",
    [string]$Duration    = "30s",
    [int]   $Vus         = 10,
    [string]$ApiKey      = "default_api_key",
    [string]$Username    = "admin",
    [string]$Password    = "admin",
    [string]$CompetitionId = "1",
    [string]$CompetitorId  = "1",
    [string]$CriteriaId    = "1",
    [string]$OutputDir   = "benchmark/results"
)

$ErrorActionPreference = "Stop"

$scriptDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path
Set-Location $projectRoot

New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$timestamp  = Get-Date -Format "yyyyMMdd-HHmmss"
$scriptPath = "benchmark/k6/scoring-compare.js"
$scenarios  = @("dashboard-history", "dashboard-criteria", "dashboard-criteria-competitor", "add-scoring")
$modes      = @("legacy", "hex")

$commonArgs = @(
    "-e", "DURATION=$Duration",
    "-e", "VUS=$Vus",
    "-e", "API_KEY=$ApiKey",
    "-e", "USERNAME=$Username",
    "-e", "PASSWORD=$Password",
    "-e", "COMPETITION_ID=$CompetitionId",
    "-e", "COMPETITOR_ID=$CompetitorId",
    "-e", "CRITERIA_ID=$CriteriaId"
)

$summaries = @{}

foreach ($scenario in $scenarios) {
    foreach ($mode in $modes) {
        $label = "$scenario-$mode"
        $summaryFile = Join-Path $OutputDir "$label-$timestamp.json"
        $summaries[$label] = $summaryFile
        $baseUrl = if ($mode -eq 'legacy') { $LegacyUrl } else { $HexUrl }

        Write-Host "Running $label ..."
        k6 run $scriptPath --summary-export $summaryFile @commonArgs -e BASE_URL=$baseUrl -e MODE=$mode -e SCENARIO=$scenario
    }
}

# Build comparison CSV
$rows = [System.Collections.Generic.List[pscustomobject]]::new()
foreach ($scenario in $scenarios) {
    foreach ($mode in $modes) {
        $label = "$scenario-$mode"
        $file  = $summaries[$label]
        $data  = Get-Content $file | ConvertFrom-Json
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