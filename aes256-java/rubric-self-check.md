# Rubric Self-Check — aes256-java

Authoritative source: `aes256-java/oop-notes/rubrics.txt`

Submission artifact under review: `Adeva_California_Rizal_PE04.zip`

## Summary

This self-check maps the delivered submission to the rubric rows exactly as named in `rubrics.txt`:

- Program Specification (40%)
- Program Execution (20%)
- Documentation (20%)
- Coding Style (10%)
- Program Additional (10%)

## Row-by-Row Check

| Rubric Row | Target From `rubrics.txt` | Project Evidence | Self-Assessment |
|---|---|---|---|
| Program Specification (40%) | "All program requirements are satisfied." | Text encrypt/decrypt, file encrypt/decrypt, OOP concept map, selftest routes, friendly errors, and flat-directory packaging are all implemented and traced in `REQUIREMENTS.md`. | Meets target |
| Program Execution (20%) | "Program runs correctly without any error." | Fresh-extract validation runs `javac *.java`, `java Main`, and `java Main --selftest` successfully from the built archive. | Meets target |
| Documentation (20%) | "Documentation is well-written and clearly explains what the code is accomplishing and how." | Submission README explains compile/run flow, CLI behavior, library usage, and OOP concept mapping; Javadocs are present on public classes and non-trivial methods; runbook and traceability artifacts capture validation evidence. | Meets target |
| Coding Style (10%) | "Code is well-written, with consistent style, and easy to follow." | Source uses consistent naming, four-space indentation, clear separation between engine/OOP/CLI layers, and no stack traces on the user path. | Meets target |
| Program Additional (10%) | "Program scores perfect in the other requirements and exceeds the specifications." | The project exceeds baseline PE scope with authenticated AES-GCM, streaming file support for arbitrary-size inputs, embeddable library wrappers, selftest harness, masked-passphrase behavior when a console is available, and friendly validated error handling. | Meets target |

## Notes

- This self-check is evidence-backed rather than aspirational: archive inspection and fresh-extract commands are recorded in Unit-04 traceability.
- The OOP concept claims trace to the canonical implementation anchors already mirrored in `Main.java` and the submission README.
- The packaging result intentionally excludes `.class` files and repository-only workflow material.

## Validation Snapshot

- Archive: `Adeva_California_Rizal_PE04.zip`
- SHA-256: `8b47782dc7d2dd9596196b33c80c14c6b7d90a24d8c77a9be757a0c9918842ae`
- Fresh-extract compile: `javac *.java` -> pass
- Fresh-extract run: `java Main` -> banner/menu shown, quit path clean
- Fresh-extract selftest: `java Main --selftest` -> `SELFTEST SUMMARY passed=6 failed=0`
