#!/usr/bin/env bash
set -euo pipefail

mode="hex"
skip_pit="false"
skip_performance="false"
base_url="http://localhost:8080"
duration="30s"
vus="10"
output_dir="target/thesis-metrics"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --mode)
      mode="$2"
      shift 2
      ;;
    --skip-pit)
      skip_pit="true"
      shift
      ;;
    --skip-performance)
      skip_performance="true"
      shift
      ;;
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
Usage: scripts/collect-thesis-metrics.sh [options]

Options:
  --mode <legacy|hex>         Spring profile mode (default: hex)
  --skip-pit                  Skip PIT mutation testing
  --skip-performance          Skip k6 benchmark comparison
  --base-url <url>            Base URL for k6 (default: http://localhost:8080)
  --duration <value>          k6 duration (default: 30s)
  --vus <int>                 k6 virtual users (default: 10)
  --output-dir <path>         Output folder (default: target/thesis-metrics)
  -h, --help                  Show this help
EOF
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      exit 1
      ;;
  esac
done

if [[ "$mode" != "legacy" && "$mode" != "hex" ]]; then
  echo "--mode must be 'legacy' or 'hex'" >&2
  exit 1
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd "$script_dir/.." && pwd)"
cd "$project_root"

profile="pam-hex"
if [[ "$mode" == "legacy" ]]; then
  profile="pam-legacy"
fi

timestamp="$(date +%Y%m%d-%H%M%S)"
run_dir="$output_dir/$mode-$timestamp"
mkdir -p "$run_dir"

echo "Running test suite for mode: $mode (profile: $profile)"
sh ./mvnw "-Dspring.profiles.active=$profile" test

echo "Running JaCoCo + dependency metrics"
sh ./mvnw -Pthesis-metrics "-Dspring.profiles.active=$profile" verify

if [[ "$skip_pit" == "false" ]]; then
  echo "Running PIT mutation analysis"
  sh ./mvnw -Pthesis-metrics "-Dspring.profiles.active=$profile" org.pitest:pitest-maven:mutationCoverage
fi

jar_file="$(ls -t target/*.jar 2>/dev/null | grep -v '\.original$' | head -n 1 || true)"
if [[ -z "$jar_file" ]]; then
  echo "No built jar found under target/." >&2
  exit 1
fi

jdeps_report="$run_dir/jdeps-$mode.txt"
echo "Collecting package dependency report with jdeps"
jdeps --ignore-missing-deps --recursive --multi-release 21 -verbose:package "$jar_file" > "$jdeps_report"

comparison_csv=""
if [[ "$skip_performance" == "false" ]]; then
  echo "Running k6 comparison benchmark"
  ./benchmark/run-k6-comparison.sh --base-url "$base_url" --duration "$duration" --vus "$vus"
  comparison_csv="$(ls -t benchmark/results/comparison-*.csv 2>/dev/null | head -n 1 || true)"
fi

summary_cmd=(
  python3 scripts/summarize-thesis-metrics.py
  --mode "$mode"
  --jacoco target/site/jacoco-global/jacoco.xml
  --pit-dir target/pit-reports
  --archunit-dir target/surefire-reports
  --jdeps "$jdeps_report"
  --output-dir "$run_dir"
)

if [[ "$skip_pit" == "true" ]]; then
  summary_cmd+=(--skip-pit)
fi

if [[ -n "$comparison_csv" ]]; then
  summary_cmd+=(--k6-comparison "$comparison_csv")
fi

"${summary_cmd[@]}"

echo "Done. Run artifacts are in $run_dir"

