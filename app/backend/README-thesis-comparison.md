# Thesis Comparison Guide (Legacy vs Hexagonal PAM)

This guide provides reusable, cross-platform automation for the three thesis dimensions:

- testability (JaCoCo + PIT)
- maintainability (ArchUnit + jdeps layer dependency report)
- performance (k6 comparison)

`Optional: contract test pass rate (legacy vs hex)` is intentionally not included in the generated thesis summary output.

<!-- thesis-metrics:start -->
## Thesis Results Snapshot

| Metric | Legacy | Hex |
|---|---:|---:|
| JaCoCo line coverage (global) | n/a | 24.22% |
| JaCoCo line coverage (hex) | n/a | 70.02% |
| JaCoCo line coverage (legacy) | n/a | 5.35% |
| JaCoCo branch coverage (global) | n/a | 19.33% |
| JaCoCo branch coverage (hex) | n/a | 36.25% |
| JaCoCo branch coverage (legacy) | n/a | 0.0% |
| JaCoCo method coverage (global) | n/a | 17.92% |
| JaCoCo method coverage (hex) | n/a | 69.72% |
| JaCoCo method coverage (legacy) | n/a | 8.12% |
| PIT mutation score | n/a | n/a |
| ArchUnit failures | n/a | 0 |
| Hex layer dependency edges | n/a | 13 |
| Hex inter-layer edges | n/a | 9 |
| Raw package dependency edges (jdeps) | n/a | 722 |
| k6 requests | n/a | n/a |
| k6 failed rate | n/a | n/a |
| k6 p95 latency (ms) | n/a | n/a |

Data source:
- legacy: not available
- hex: `app/backend/target/thesis-metrics/hex-20260406-132441/summary-hex.json`
<!-- thesis-metrics:end -->

## Prerequisites

- Java 21 (with `jdeps`)
- Python 3
- k6 (if performance is enabled)

## 1) Start backend in a comparable mode

### Windows (PowerShell)

```powershell
cd app/backend
./mvnw.cmd spring-boot:run "-Dspring-boot.run.mainClass=com.arbitaja.backend.ArbitajaLegacyPamApplication"
```

```powershell
cd app/backend
./mvnw.cmd spring-boot:run "-Dspring-boot.run.mainClass=com.arbitaja.backend.ArbitajaHexPamApplication"
```

### macOS / Linux (bash)

```bash
cd app/backend
sh ./mvnw spring-boot:run "-Dspring-boot.run.mainClass=com.arbitaja.backend.ArbitajaLegacyPamApplication"
```

```bash
cd app/backend
sh ./mvnw spring-boot:run "-Dspring-boot.run.mainClass=com.arbitaja.backend.ArbitajaHexPamApplication"
```

## 2) Collect automated thesis metrics

### Windows (PowerShell)

```powershell
cd app/backend
./scripts/collect-thesis-metrics.ps1 -Mode legacy
./scripts/collect-thesis-metrics.ps1 -Mode hex
```

### macOS / Linux (bash)

```bash
cd app/backend
./scripts/collect-thesis-metrics.sh --mode legacy
./scripts/collect-thesis-metrics.sh --mode hex
```

Useful flags:

- skip mutation testing: `-SkipPit` (PowerShell) or `--skip-pit` (bash)
- skip performance run: `-SkipPerformance` (PowerShell) or `--skip-performance` (bash)
- override k6 load: `-Duration/-Vus` or `--duration/--vus`

## 3) Output artifacts

Each run writes timestamped artifacts under `target/thesis-metrics/<mode>-<timestamp>`:

- `summary-<mode>.md` (thesis-friendly summary)
- `summary-<mode>.csv` (spreadsheet import)
- `summary-<mode>.json` (machine-readable)
- `jdeps-<mode>.txt` (raw package dependency data)

Generated report sources used by the summary:

- JaCoCo global: `target/site/jacoco-global/jacoco.xml`
- JaCoCo hex-only: `target/site/jacoco-hex/jacoco.xml`
- JaCoCo legacy-only: `target/site/jacoco-legacy/jacoco.xml`
- PIT: `target/pit-reports/**/mutations.xml`
- ArchUnit: `target/surefire-reports/TEST-*HexagonalArchitectureTest.xml`
- k6 comparison CSV: `benchmark/results/comparison-*.csv`

## 4) Run only the performance comparison

### Windows (PowerShell)

```powershell
cd app/backend
./benchmark/run-k6-comparison.ps1 -BaseUrl http://localhost:8080 -Duration 30s -Vus 10
```

### macOS / Linux (bash)

```bash
cd app/backend
./benchmark/run-k6-comparison.sh --base-url http://localhost:8080 --duration 30s --vus 10
```

