#!/usr/bin/env bash
set -euo pipefail

legacy_url="http://localhost:9090"
hex_url="http://localhost:8080"
duration="30s"
vus="10"
api_key="arbitaja"
username="admin"
password="arbitaja"
competition_id="1"
competitor_id="1"
criteria_id="1"
output_dir="benchmark/results"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --legacy-url)    legacy_url="$2";    shift 2 ;;
    --hex-url)       hex_url="$2";       shift 2 ;;
    --duration)      duration="$2";      shift 2 ;;
    --vus)           vus="$2";           shift 2 ;;
    --api-key)       api_key="$2";       shift 2 ;;
    --username)      username="$2";      shift 2 ;;
    --password)      password="$2";      shift 2 ;;
    --competition-id) competition_id="$2"; shift 2 ;;
    --competitor-id)  competitor_id="$2";  shift 2 ;;
    --criteria-id)    criteria_id="$2";    shift 2 ;;
    --output-dir)    output_dir="$2";    shift 2 ;;
    -h|--help)
      cat <<'EOF'
Usage: benchmark/run-k6-scoring-comparison.sh [options]

Options:
  --legacy-url <url>       Legacy backend URL (default: http://localhost:9090)
  --hex-url <url>          Hex backend URL (default: http://localhost:8080)
  --duration <value>       k6 duration (default: 30s)
  --vus <int>              k6 virtual users (default: 10)
  --api-key <key>          API key for scored endpoints (default: arbitaja)
  --username <user>        Login username for session auth (default: admin)
  --password <pass>        Login password (default: arbitaja)
  --competition-id <id>    Competition ID to use in requests (default: 1)
  --competitor-id <id>     Competitor ID for add-scoring (default: 1)
  --criteria-id <id>       Criteria ID for add-scoring (default: 1)
  --output-dir <path>      Results folder (default: benchmark/results)
  -h, --help               Show this help
EOF
      exit 0
      ;;
    *) echo "Unknown argument: $1" >&2; exit 1 ;;
  esac
done

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd "$script_dir/.." && pwd)"
cd "$project_root"

mkdir -p "$output_dir"
timestamp="$(date +%Y%m%d-%H%M%S)"
script_path="benchmark/k6/scoring-compare.js"

scenarios=("dashboard-history" "dashboard-criteria" "dashboard-criteria-competitor" "add-scoring")
modes=("legacy" "hex")

declare -A summaries

common_args=(
  -e "DURATION=$duration"
  -e "VUS=$vus"
  -e "API_KEY=$api_key"
  -e "USERNAME=$username"
  -e "PASSWORD=$password"
  -e "COMPETITION_ID=$competition_id"
  -e "COMPETITOR_ID=$competitor_id"
  -e "CRITERIA_ID=$criteria_id"
)

for scenario in "${scenarios[@]}"; do
  for mode in "${modes[@]}"; do
    label="${scenario}-${mode}"
    summary_file="$output_dir/${label}-${timestamp}.json"
    summaries[$label]="$summary_file"
    base_url=$([ "$mode" = "legacy" ] && echo "$legacy_url" || echo "$hex_url")

    echo "Running ${label} ..."
    k6 run "$script_path" --summary-export "$summary_file" \
      "${common_args[@]}" \
      -e "BASE_URL=$base_url" \
      -e "MODE=$mode" \
      -e "SCENARIO=$scenario"
  done
done

comparison_csv="$output_dir/scoring-comparison-$timestamp.csv"

python3 - "${summaries[@]}" "$comparison_csv" <<'PY'
import csv, json, sys

# Last arg is the output file; all before are summary JSON files
*summary_files, out_file = sys.argv[1:]

rows = []
for path in summary_files:
    # Derive label from filename: e.g. "dashboard-history-legacy-20260509-120000.json"
    import os
    name = os.path.basename(path)
    # strip timestamp suffix and .json: "dashboard-history-legacy"
    label = "-".join(name.split("-")[:-2])
    parts = label.rsplit("-", 1)
    scenario_part = parts[0] if len(parts) == 2 else label
    mode_part     = parts[1] if len(parts) == 2 else "unknown"

    with open(path, encoding="utf-8") as f:
        data = json.load(f)

    m = data["metrics"]
    rows.append({
        "scenario":   scenario_part,
        "mode":       mode_part,
        "requests":   m["http_reqs"]["values"]["count"],
        "failedRate": m["http_req_failed"]["values"]["rate"],
        "p50Ms":      m["http_req_duration"]["values"]["p(50)"],
        "p95Ms":      m["http_req_duration"]["values"]["p(95)"],
        "avgMs":      m["http_req_duration"]["values"]["avg"],
    })

rows.sort(key=lambda r: (r["scenario"], r["mode"]))

with open(out_file, "w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=["scenario", "mode", "requests", "failedRate", "p50Ms", "p95Ms", "avgMs"])
    writer.writeheader()
    writer.writerows(rows)
PY

echo ""
echo "Results written to $output_dir"
echo "Comparison CSV: $comparison_csv"