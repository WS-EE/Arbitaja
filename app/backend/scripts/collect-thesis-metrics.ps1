param(
    [string]$Mode = "hex",
    [switch]$SkipPit,
    [switch]$SkipPerformance,
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Duration = "30s",
    [int]$Vus = 10,
    [string]$OutputDir = "target/thesis-metrics"
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path
Set-Location $projectRoot

$profile = if ($Mode -eq "legacy") { "pam-legacy" } else { "pam-hex" }
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$runDir = Join-Path $OutputDir "$Mode-$timestamp"
New-Item -ItemType Directory -Path $runDir -Force | Out-Null

Write-Host "Running test suite for mode: $Mode (profile: $profile)"
./mvnw.cmd "-Dspring.profiles.active=$profile" test

Write-Host "Running JaCoCo + dependency metrics"
./mvnw.cmd -Pthesis-metrics "-Dspring.profiles.active=$profile" verify

if (-not $SkipPit) {
    Write-Host "Running PIT mutation analysis"
    ./mvnw.cmd -Pthesis-metrics "-Dspring.profiles.active=$profile" org.pitest:pitest-maven:mutationCoverage
}

$jarFile = Get-ChildItem -Path "target" -Filter "*.jar" |
    Where-Object { $_.Name -notlike "*.original" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $jarFile) {
    throw "No built jar found under target/."
}

$jdepsReport = Join-Path $runDir "jdeps-$Mode.txt"
Write-Host "Collecting package dependency report with jdeps"
jdeps --ignore-missing-deps --recursive --multi-release 21 -verbose:package $jarFile.FullName | Out-File -FilePath $jdepsReport -Encoding utf8

$comparisonCsv = ""
if (-not $SkipPerformance) {
    Write-Host "Running k6 comparison benchmark"
    ./benchmark/run-k6-comparison.ps1 -BaseUrl $BaseUrl -Duration $Duration -Vus $Vus
    $comparisonCsv = (Get-ChildItem -Path "benchmark/results" -Filter "comparison-*.csv" |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1).FullName
}

$summaryArgs = @(
    "scripts/summarize-thesis-metrics.py",
    "--mode", $Mode,
    "--jacoco", "target/site/jacoco-global/jacoco.xml",
    "--pit-dir", "target/pit-reports",
    "--archunit-dir", "target/surefire-reports",
    "--jdeps", $jdepsReport,
    "--output-dir", $runDir
)

if ($SkipPit) {
    $summaryArgs += "--skip-pit"
}

if ($comparisonCsv) {
    $summaryArgs += @("--k6-comparison", $comparisonCsv)
}

if (Get-Command python3 -ErrorAction SilentlyContinue) {
    & python3 @summaryArgs
} elseif (Get-Command python -ErrorAction SilentlyContinue) {
    & python @summaryArgs
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    & py -3 @summaryArgs
} else {
    throw "Python 3 is required to generate summary artifacts."
}

Write-Host "Done. Run artifacts are in $runDir"

