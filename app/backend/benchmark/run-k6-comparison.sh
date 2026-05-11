#!/usr/bin/env bash
set -euo pipefail

legacy_url="http://localhost:9090"
hex_url="http://localhost:8080"
duration="30s"
vus="10"
username="admin"
password="admin"
user_id="1"
output_dir="benchmark/results"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --legacy-url) legacy_url="$2"; shift 2 ;;
    --hex-url)    hex_url="$2";    shift 2 ;;
    --duration)   duration="$2";   shift 2 ;;
    --vus)        vus="$2";        shift 2 ;;
    --username)   username="$2";   shift 2 ;;
    --password)   password="$2";   shift 2 ;;
    --user-id)    user_id="$2";    shift 2 ;;
    --output-dir) output_dir="$2"; shift 2 ;;
    -h|--help)
      cat <<'EOF'
Usage: benchmark/run-k6-comparison.sh [options]

Options:
  --legacy-url <url>    Legacy backend URL (default: http://localhost:9090)
  --hex-url <url>       Hex backend URL (default: http://localhost:8080)
  --duration <value>    k6 duration (default: 30s)
  --vus <int>           k6 virtual users (default: 10)
  --username <user>     Login username (default: admin)
  --password <pass>     Login password (default: admin)
  --user-id <id>        User ID for user-profile scenario (default: 1)
  --output-dir <path>   Results folder (default: benchmark/results)
  -h, --help            Show this help
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
script_path="benchmark/k6/pam-compare.js"

scenarios=("signup" "list-users" "user-profile")
modes=("legacy" "hex")

declare -A summaries

common_args=(
  -e "DURATION=$duration"
  -e "VUS=$vus"
  -e "USERNAME=$username"
  -e "PASSWORD=$password"
  -e "USER_ID=$user_id"
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

comparison_csv="$output_dir/pam-comparison-$timestamp.csv"

python3 - "${summaries[@]}" "$comparison_csv" <<'PY'
import csv, json, sys, os

*summary_files, out_file = sys.argv[1:]

rows = []
for path in summary_files:
    name = os.path.basename(path)
    # filename: {scenario}-{mode}-{YYYYMMDD}-{HHMMSS}.json
    # drop last 2 dash-parts (timestamp) then split off trailing mode word
    parts = name[:-5].split("-")   # strip .json, split by -
    label = "-".join(parts[:-2])   # drop YYYYMMDD and HHMMSS
    scenario_part, mode_part = label.rsplit("-", 1)

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
