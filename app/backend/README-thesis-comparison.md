# Thesis Comparison Guide (Legacy vs Hexagonal PAM)

This guide describes how to run the backend in standalone PAM modes and collect measurable comparison metrics.

## 1) Start one mode standalone

### Legacy PAM mode

```powershell
cd app/backend
./mvnw.cmd spring-boot:run "-Dspring-boot.run.mainClass=com.arbitaja.backend.ArbitajaLegacyPamApplication"
```

### Hexagonal PAM mode

```powershell
cd app/backend
./mvnw.cmd spring-boot:run "-Dspring-boot.run.mainClass=com.arbitaja.backend.ArbitajaHexPamApplication"
```

Both launchers start the same app but force different Spring profiles:

- `pam-legacy` -> `arbitaja.pam.mode=legacy`
- `pam-hex` -> `arbitaja.pam.mode=hex`

## 2) Run shared contract tests

The contract suite lives under `src/test/java/com/arbitaja/thesis/contract` and validates shared signup-list behavior for both variants with mocked dependencies.

```powershell
cd app/backend
./mvnw.cmd "-Dtest=*SignupContractTest" test
```

## 3) Collect modularity and quality metrics

Run coverage (JaCoCo), dependency metrics (Maven dependency analyzer), and optional mutation testing (PIT).

```powershell
cd app/backend
./scripts/collect-thesis-metrics.ps1 -Mode legacy -SkipPit
./scripts/collect-thesis-metrics.ps1 -Mode hex -SkipPit
```

To include mutation testing, remove `-SkipPit`.

## 4) Run performance benchmark with k6

```powershell
cd app/backend
./benchmark/run-k6-comparison.ps1 -BaseUrl http://localhost:8080 -Duration 30s -Vus 10
```

The script writes mode-specific JSON summaries and one comparison CSV in `benchmark/results`.

