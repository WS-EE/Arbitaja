param(
    [string]$Mode = "hex",
    [switch]$SkipPit
)

$ErrorActionPreference = "Stop"

$profile = if ($Mode -eq "legacy") { "pam-legacy" } else { "pam-hex" }

Write-Host "Running test suite for mode: $Mode (profile: $profile)"
./mvnw.cmd "-Dspring.profiles.active=$profile" test

Write-Host "Running JaCoCo + dependency metrics"
./mvnw.cmd -Pthesis-metrics "-Dspring.profiles.active=$profile" verify

if (-not $SkipPit) {
    Write-Host "Running PIT mutation analysis"
    ./mvnw.cmd -Pthesis-metrics "-Dspring.profiles.active=$profile" org.pitest:pitest-maven:mutationCoverage
}

Write-Host "Done. Reports are in target/site and target/pit-reports."

