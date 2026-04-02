# Backend Thesis Comparison Benchmarking

This folder contains a reproducible benchmark harness for comparing legacy PAM endpoints against the refactored hexagonal PAM endpoints.

## Prerequisites

- Backend running locally (`http://localhost:8080` by default)
- `k6` installed and available on `PATH`

## Endpoints used

- `legacy` mode: `POST /v1/user/signup/create`
- `hex` mode: `POST /v2/signup`

## Run one-off benchmark manually

```powershell
cd app/backend
k6 run benchmark/k6/pam-compare.js --summary-export benchmark/results/legacy.json -e MODE=legacy -e BASE_URL=http://localhost:8080 -e DURATION=30s -e VUS=10
k6 run benchmark/k6/pam-compare.js --summary-export benchmark/results/hex.json -e MODE=hex -e BASE_URL=http://localhost:8080 -e DURATION=30s -e VUS=10
```

## Run both modes and export comparison CSV

```powershell
cd app/backend
./benchmark/run-k6-comparison.ps1 -BaseUrl http://localhost:8080 -Duration 30s -Vus 10
```

The script writes:

- mode-specific k6 summaries (`*.json`)
- a compact comparison table (`comparison-*.csv`) with request count, failed rate, and p95 latency

