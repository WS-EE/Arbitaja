#!/usr/bin/env bash
set -euo pipefail

base_url="http://localhost:8080"
duration="30s"
vus="10"
output_dir="benchmark/results"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --base-url)
      base_url="$2"
      shift 2
      ;;
    --duration)
      duration="$2"
      shift 2
      ;;
    --vus)
      vus="$2"
      shift 2
      ;;
    --output-dir)
      output_dir="$2"
      shift 2
      ;;
    -h|--help)
      cat <<'EOF'
Usage: benchmark/run-k6-comparison.sh [options]

Options:
  --base-url <url>      Backend URL (default: http://localhost:8080)
  --duration <value>    k6 duration (default: 30s)
  --vus <int>           k6 virtual users (default: 10)
  --output-dir <path>   Results folder (default: benchmark/results)
  -h, --help            Show this help
EOF
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      exit 1
      ;;
  esac
done

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd "$script_dir/.." && pwd)"
cd "$project_root"

mkdir -p "$output_dir"
timestamp="$(date +%Y%m%d-%H%M%S)"
script_path="benchmark/k6/pam-compare.js"
legacy_summary="$output_dir/legacy-summary-$timestamp.json"
hex_summary="$output_dir/hex-summary-$timestamp.json"
comparison_csv="$output_dir/comparison-$timestamp.csv"

echo "Running legacy benchmark..."
k6 run "$script_path" --summary-export "$legacy_summary" -e MODE=legacy -e BASE_URL="$base_url" -e DURATION="$duration" -e VUS="$vus"

echo "Running hex benchmark..."
k6 run "$script_path" --summary-export "$hex_summary" -e MODE=hex -e BASE_URL="$base_url" -e DURATION="$duration" -e VUS="$vus"

python3 - "$legacy_summary" "$hex_summary" "$comparison_csv" <<'PY'
import csv
import json
import sys

legacy_file, hex_file, out_file = sys.argv[1:4]

def row(mode, path):
    with open(path, encoding="utf-8") as f:
        data = json.load(f)
    return {
        "mode": mode,
        "requests": data["metrics"]["http_reqs"]["values"]["count"],
        "failedRate": data["metrics"]["http_req_failed"]["values"]["rate"],
        "p95Ms": data["metrics"]["http_req_duration"]["values"]["p(95)"],
    }

rows = [row("legacy", legacy_file), row("hex", hex_file)]
with open(out_file, "w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=["mode", "requests", "failedRate", "p95Ms"])
    writer.writeheader()
    writer.writerows(rows)
PY

echo "Benchmark summaries written to $output_dir"
echo "Comparison CSV: $comparison_csv"

