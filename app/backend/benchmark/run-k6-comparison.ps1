param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Duration = "30s",
    [int]$Vus = 10,
    [string]$OutputDir = "benchmark/results"
)

$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$scriptPath = "benchmark/k6/pam-compare.js"

$legacySummary = Join-Path $OutputDir "legacy-summary-$timestamp.json"
$hexSummary = Join-Path $OutputDir "hex-summary-$timestamp.json"
$comparisonCsv = Join-Path $OutputDir "comparison-$timestamp.csv"

Write-Host "Running legacy benchmark..."
k6 run $scriptPath --summary-export $legacySummary -e MODE=legacy -e BASE_URL=$BaseUrl -e DURATION=$Duration -e VUS=$Vus

Write-Host "Running hex benchmark..."
k6 run $scriptPath --summary-export $hexSummary -e MODE=hex -e BASE_URL=$BaseUrl -e DURATION=$Duration -e VUS=$Vus

$legacy = Get-Content $legacySummary | ConvertFrom-Json
$hex = Get-Content $hexSummary | ConvertFrom-Json

$rows = @(
    [pscustomobject]@{
        mode = "legacy"
        requests = $legacy.metrics.http_reqs.values.count
        failedRate = $legacy.metrics.http_req_failed.values.rate
        p95Ms = $legacy.metrics.http_req_duration.values."p(95)"
    },
    [pscustomobject]@{
        mode = "hex"
        requests = $hex.metrics.http_reqs.values.count
        failedRate = $hex.metrics.http_req_failed.values.rate
        p95Ms = $hex.metrics.http_req_duration.values."p(95)"
    }
)

$rows | Export-Csv -Path $comparisonCsv -NoTypeInformation
Write-Host "Benchmark summaries written to $OutputDir"
Write-Host "Comparison CSV: $comparisonCsv"

