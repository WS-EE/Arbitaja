#!/usr/bin/env python3
import argparse
import csv
import json
import re
from pathlib import Path
import xml.etree.ElementTree as ET

HEX_LAYER_PREFIXES = {
    "domain": "com.arbitaja.refactored.backend.pam.core.domain",
    "application": "com.arbitaja.refactored.backend.pam.core.application",
    "ports_in": "com.arbitaja.refactored.backend.pam.core.port.in",
    "ports_out": "com.arbitaja.refactored.backend.pam.core.port.out",
    "adapter_in": "com.arbitaja.refactored.backend.pam.adapter.in",
    "adapter_out": "com.arbitaja.refactored.backend.pam.adapter.out",
}

JACOCO_SCOPES = {
    "global": [""],
    "hex": ["com/arbitaja/refactored/backend/pam/"],
    "legacy": ["com/arbitaja/backend/users/"],
}


def pct(covered: int, missed: int) -> float:
    total = covered + missed
    if total == 0:
        return 0.0
    return round((covered / total) * 100.0, 2)


def parse_jacoco(jacoco_xml: Path) -> dict:
    root = ET.parse(jacoco_xml).getroot()

    def counters_for_scope(prefixes: list[str]) -> dict[str, int]:
        if prefixes == [""]:
            counters = {c.attrib["type"]: c.attrib for c in root.findall("counter")}
            return {
                "LINE_covered": int(counters.get("LINE", {}).get("covered", 0)),
                "LINE_missed": int(counters.get("LINE", {}).get("missed", 0)),
                "BRANCH_covered": int(counters.get("BRANCH", {}).get("covered", 0)),
                "BRANCH_missed": int(counters.get("BRANCH", {}).get("missed", 0)),
                "METHOD_covered": int(counters.get("METHOD", {}).get("covered", 0)),
                "METHOD_missed": int(counters.get("METHOD", {}).get("missed", 0)),
            }

        totals = {
            "LINE_covered": 0,
            "LINE_missed": 0,
            "BRANCH_covered": 0,
            "BRANCH_missed": 0,
            "METHOD_covered": 0,
            "METHOD_missed": 0,
        }

        for package in root.findall("package"):
            pkg_name = package.attrib.get("name", "")
            if not any(pkg_name.startswith(prefix) for prefix in prefixes):
                continue

            for counter_type in ("LINE", "BRANCH", "METHOD"):
                counter = package.find(f"counter[@type='{counter_type}']")
                if counter is None:
                    continue
                totals[f"{counter_type}_covered"] += int(counter.attrib.get("covered", 0))
                totals[f"{counter_type}_missed"] += int(counter.attrib.get("missed", 0))

        return totals

    result = {}
    for scope, prefixes in JACOCO_SCOPES.items():
        counters = counters_for_scope(prefixes)
        result[f"jacoco_{scope}_line_coverage_pct"] = pct(counters["LINE_covered"], counters["LINE_missed"])
        result[f"jacoco_{scope}_branch_coverage_pct"] = pct(counters["BRANCH_covered"], counters["BRANCH_missed"])
        result[f"jacoco_{scope}_method_coverage_pct"] = pct(counters["METHOD_covered"], counters["METHOD_missed"])

    # Backward compatibility fields
    result["jacoco_line_coverage_pct"] = result["jacoco_global_line_coverage_pct"]
    result["jacoco_branch_coverage_pct"] = result["jacoco_global_branch_coverage_pct"]
    result["jacoco_method_coverage_pct"] = result["jacoco_global_method_coverage_pct"]

    return result


def latest_mutations_xml(pit_dir: Path) -> Path | None:
    if not pit_dir.exists():
        return None

    candidates = sorted(pit_dir.glob("**/mutations.xml"), key=lambda p: p.stat().st_mtime, reverse=True)
    return candidates[0] if candidates else None


def parse_pit(pit_dir: Path) -> dict:
    xml_path = latest_mutations_xml(pit_dir)
    if xml_path is None:
        return {"pit_mutation_score_pct": None, "pit_mutations_total": 0}

    root = ET.parse(xml_path).getroot()
    mutations = root.findall("mutation")
    total = len(mutations)
    detected = sum(1 for m in mutations if m.attrib.get("status") == "KILLED")

    return {
        "pit_mutation_score_pct": round((detected / total) * 100.0, 2) if total else 0.0,
        "pit_mutations_total": total,
    }


def parse_archunit(archunit_dir: Path) -> dict:
    if not archunit_dir.exists():
        return {"archunit_tests": 0, "archunit_failures": 0, "archunit_errors": 0}

    test_reports = sorted(archunit_dir.glob("TEST-*.xml"))
    arch_reports = [p for p in test_reports if "HexagonalArchitectureTest" in p.name]

    tests = 0
    failures = 0
    errors = 0
    for report in arch_reports:
        root = ET.parse(report).getroot()
        tests += int(root.attrib.get("tests", 0))
        failures += int(root.attrib.get("failures", 0))
        errors += int(root.attrib.get("errors", 0))

    return {
        "archunit_tests": tests,
        "archunit_failures": failures,
        "archunit_errors": errors,
    }


def classify_hex_layer(package_name: str) -> str | None:
    for layer, prefix in HEX_LAYER_PREFIXES.items():
        if package_name.startswith(prefix):
            return layer
    return None


def parse_jdeps(jdeps_report: Path) -> dict:
    if not jdeps_report.exists():
        return {
            "hex_layer_dependency_edges": 0,
            "hex_layer_interlayer_edges": 0,
            "hex_layer_edge_details": [],
        }

    edge_pattern = re.compile(r"^\s*([A-Za-z0-9_$.]+)\s+->\s+([A-Za-z0-9_$.]+)")
    layer_edges = set()
    raw_edges = 0

    for line in jdeps_report.read_text(encoding="utf-8", errors="ignore").splitlines():
        match = edge_pattern.search(line)
        if not match:
            continue

        raw_edges += 1
        from_pkg = match.group(1)
        to_pkg = match.group(2)
        from_layer = classify_hex_layer(from_pkg)
        to_layer = classify_hex_layer(to_pkg)
        if from_layer and to_layer:
            layer_edges.add((from_layer, to_layer))

    interlayer_edges = sorted(e for e in layer_edges if e[0] != e[1])
    return {
        "hex_layer_dependency_edges": len(layer_edges),
        "hex_layer_interlayer_edges": len(interlayer_edges),
        "hex_layer_edge_details": [f"{src}->{dst}" for src, dst in interlayer_edges],
        "jdeps_raw_package_edges": raw_edges,
    }


def parse_k6_comparison(csv_path: Path, mode: str) -> dict:
    if not csv_path.exists():
        return {"k6_requests": None, "k6_failed_rate": None, "k6_p95_ms": None}

    with csv_path.open(newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            if row.get("mode") == mode:
                return {
                    "k6_requests": float(row.get("requests", 0)),
                    "k6_failed_rate": float(row.get("failedRate", 0)),
                    "k6_p95_ms": float(row.get("p95Ms", 0)),
                }

    return {"k6_requests": None, "k6_failed_rate": None, "k6_p95_ms": None}


def write_markdown(path: Path, summary: dict) -> None:
    lines = [
        f"# Thesis Metrics Summary ({summary['mode']})",
        "",
        "| Metric | Value |",
        "|---|---:|",
    ]

    for key in [
        "jacoco_global_line_coverage_pct",
        "jacoco_hex_line_coverage_pct",
        "jacoco_legacy_line_coverage_pct",
        "jacoco_global_branch_coverage_pct",
        "jacoco_hex_branch_coverage_pct",
        "jacoco_legacy_branch_coverage_pct",
        "jacoco_global_method_coverage_pct",
        "jacoco_hex_method_coverage_pct",
        "jacoco_legacy_method_coverage_pct",
        "jacoco_line_coverage_pct",
        "jacoco_branch_coverage_pct",
        "jacoco_method_coverage_pct",
        "pit_mutation_score_pct",
        "pit_mutations_total",
        "archunit_tests",
        "archunit_failures",
        "archunit_errors",
        "hex_layer_dependency_edges",
        "hex_layer_interlayer_edges",
        "jdeps_raw_package_edges",
        "k6_requests",
        "k6_failed_rate",
        "k6_p95_ms",
    ]:
        value = summary.get(key)
        value_text = "n/a" if value is None else value
        lines.append(f"| {key} | {value_text} |")

    if summary.get("hex_layer_edge_details"):
        lines.append("")
        lines.append("## Hexagonal Layer Dependency Edges")
        for edge in summary["hex_layer_edge_details"]:
            lines.append(f"- {edge}")

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def write_csv(path: Path, summary: dict) -> None:
    with path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["metric", "value"])
        for key, value in summary.items():
            if isinstance(value, list):
                writer.writerow([key, ";".join(value)])
            else:
                writer.writerow([key, value if value is not None else ""])


def main() -> None:
    parser = argparse.ArgumentParser(description="Summarize thesis comparison metrics into JSON/CSV/Markdown.")
    parser.add_argument("--mode", required=True, choices=["legacy", "hex"])
    parser.add_argument("--jacoco", required=True)
    parser.add_argument("--pit-dir", required=True)
    parser.add_argument("--archunit-dir", required=True)
    parser.add_argument("--jdeps", required=True)
    parser.add_argument("--output-dir", required=True)
    parser.add_argument("--k6-comparison")
    parser.add_argument("--skip-pit", action="store_true")
    args = parser.parse_args()

    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    summary = {"mode": args.mode}
    summary.update(parse_jacoco(Path(args.jacoco)))
    if args.skip_pit:
        summary.update({"pit_mutation_score_pct": None, "pit_mutations_total": 0})
    else:
        summary.update(parse_pit(Path(args.pit_dir)))
    summary.update(parse_archunit(Path(args.archunit_dir)))
    summary.update(parse_jdeps(Path(args.jdeps)))

    if args.k6_comparison:
        summary.update(parse_k6_comparison(Path(args.k6_comparison), args.mode))
    else:
        summary.update({"k6_requests": None, "k6_failed_rate": None, "k6_p95_ms": None})

    json_path = output_dir / f"summary-{args.mode}.json"
    csv_path = output_dir / f"summary-{args.mode}.csv"
    md_path = output_dir / f"summary-{args.mode}.md"

    json_path.write_text(json.dumps(summary, indent=2), encoding="utf-8")
    write_csv(csv_path, summary)
    write_markdown(md_path, summary)

    print(f"Summary written to {json_path}")
    print(f"Summary written to {csv_path}")
    print(f"Summary written to {md_path}")


if __name__ == "__main__":
    main()

