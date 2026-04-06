#!/usr/bin/env python3
import argparse
import json
from pathlib import Path

START_MARKER = "<!-- thesis-metrics:start -->"
END_MARKER = "<!-- thesis-metrics:end -->"

METRICS = [
    ("jacoco_global_line_coverage_pct", "JaCoCo line coverage (global)", "pct"),
    ("jacoco_hex_line_coverage_pct", "JaCoCo line coverage (hex)", "pct"),
    ("jacoco_legacy_line_coverage_pct", "JaCoCo line coverage (legacy)", "pct"),
    ("jacoco_global_branch_coverage_pct", "JaCoCo branch coverage (global)", "pct"),
    ("jacoco_hex_branch_coverage_pct", "JaCoCo branch coverage (hex)", "pct"),
    ("jacoco_legacy_branch_coverage_pct", "JaCoCo branch coverage (legacy)", "pct"),
    ("jacoco_global_method_coverage_pct", "JaCoCo method coverage (global)", "pct"),
    ("jacoco_hex_method_coverage_pct", "JaCoCo method coverage (hex)", "pct"),
    ("jacoco_legacy_method_coverage_pct", "JaCoCo method coverage (legacy)", "pct"),
    ("pit_mutation_score_pct", "PIT mutation score", "pct"),
    ("archunit_failures", "ArchUnit failures", "num"),
    ("hex_layer_dependency_edges", "Hex layer dependency edges", "num"),
    ("hex_layer_interlayer_edges", "Hex inter-layer edges", "num"),
    ("jdeps_raw_package_edges", "Raw package dependency edges (jdeps)", "num"),
    ("k6_requests", "k6 requests", "num"),
    ("k6_failed_rate", "k6 failed rate", "num"),
    ("k6_p95_ms", "k6 p95 latency (ms)", "num"),
]


def latest_summary(metrics_root: Path, mode: str) -> Path | None:
    candidates = sorted(
        metrics_root.glob(f"{mode}-*/summary-{mode}.json"),
        key=lambda p: p.stat().st_mtime,
        reverse=True,
    )
    return candidates[0] if candidates else None


def load_summary(path: Path | None) -> dict | None:
    if path is None or not path.exists():
        return None
    return json.loads(path.read_text(encoding="utf-8"))


def format_value(value, kind: str) -> str:
    if value is None:
        return "n/a"
    if kind == "pct":
        return f"{value}%"
    return str(value)


def generated_block(legacy: dict | None, hex_data: dict | None, legacy_path: Path | None, hex_path: Path | None) -> str:
    lines = [
        START_MARKER,
        "## Thesis Results Snapshot",
        "",
        "| Metric | Legacy | Hex |",
        "|---|---:|---:|",
    ]

    for key, label, kind in METRICS:
        legacy_value = format_value(None if legacy is None else legacy.get(key), kind)
        hex_value = format_value(None if hex_data is None else hex_data.get(key), kind)
        lines.append(f"| {label} | {legacy_value} | {hex_value} |")

    lines.extend(["", "Data source:"])
    if legacy_path is not None:
        lines.append(f"- legacy: `{legacy_path.as_posix()}`")
    else:
        lines.append("- legacy: not available")

    if hex_path is not None:
        lines.append(f"- hex: `{hex_path.as_posix()}`")
    else:
        lines.append("- hex: not available")

    lines.append(END_MARKER)
    return "\n".join(lines)


def replace_marked_block(readme_text: str, block: str) -> str:
    start = readme_text.find(START_MARKER)
    end = readme_text.find(END_MARKER)

    if start == -1 or end == -1 or end < start:
        suffix = "" if readme_text.endswith("\n") else "\n"
        return f"{readme_text}{suffix}\n{block}\n"

    end_inclusive = end + len(END_MARKER)
    return readme_text[:start] + block + readme_text[end_inclusive:]


def main() -> None:
    parser = argparse.ArgumentParser(description="Render thesis results snapshot into the root README.")
    parser.add_argument("--readme", required=True)
    parser.add_argument("--metrics-root", required=True)
    args = parser.parse_args()

    readme_path = Path(args.readme)
    metrics_root = Path(args.metrics_root)

    legacy_path = latest_summary(metrics_root, "legacy")
    hex_path = latest_summary(metrics_root, "hex")

    legacy_data = load_summary(legacy_path)
    hex_data = load_summary(hex_path)

    block = generated_block(legacy_data, hex_data, legacy_path, hex_path)
    current = readme_path.read_text(encoding="utf-8")
    updated = replace_marked_block(current, block)
    readme_path.write_text(updated, encoding="utf-8")

    print(f"Updated {readme_path}")


if __name__ == "__main__":
    main()

