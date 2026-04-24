# Version 0.1.3 Documentation

## Title
Unit 02 / Bolt 2.3: In-Program Assertion Runner and Dual Entry Hooks

## Quick Diagnostic Read

`v0.1.3` completes Unit-02 selftest integration by introducing a reusable `SelfTest` runner and making it reachable from both required entry paths:

- `java Main --selftest`
- menu option `5` inside `Main`

This version is focused on executable validation and routing, not full interactive encrypt/decrypt CLI wiring.

## One-Sentence Objective

Implement and verify `US-10` by adding a JDK-only selftest harness that reports pass/fail counts and is invokable from both command-flag and menu routes.

## Scope and Versioning

- Previous documented version: `v0.1.2`
- Current documented version: `v0.1.3`
- Release type: pre-alpha patch milestone for Unit-02 selftest integration

## File-Level Change Summary

1. `aes256-java/SelfTest.java`

- Added in-program assertion runner with:
  - deterministic test execution flow,
  - pass/fail counters and summary output,
  - exit-code policy (`0` all pass, non-zero otherwise).
- Includes validation cases aligned with Unit-01/Unit-02 behavior:
  - text round-trip checks,
  - tamper detection,
  - wrong-passphrase rejection,
  - overwrite refusal,
  - stream round-trip smoke check (plus optional large mode).

2. `aes256-java/Main.java`

- Added minimal CLI entry routing for this bolt:
  - `--selftest` and `--selftest-large` direct paths,
  - interactive menu with option `5` invoking selftest.
- Non-selftest menu actions are placeholder outputs and remain scheduled for Unit-03.

3. `README.md`

- Bumped visible version/status to `v0.1.3`.
- Updated feature and roadmap sections to include BOLT-2.3 completion.
- Updated baseline usage to mention direct selftest invocation.

4. `CHANGELOG.md`

- Added `v0.1.3` entry with implementation scope, validation notes, and build-artifact deletion candidates.

## Validation Notes

Validation executed for this version scope:

- Compile gate:
  - `javac *.java` in `aes256-java/` passed.
- `TEST-10` flag path:
  - `java Main --selftest` exit code `0`.
  - Output summary: `SELFTEST SUMMARY passed=6 failed=0`.
- `TEST-10` menu path:
  - scripted input (`5`, then `0`) to `java Main` executed selftest and returned exit code `0`.
  - Output includes `selftest exit code=0`.
- Optional large stream path:
  - `java Main --selftest-large` passed and reported `PASS TEST-03 round-trip 128MiB`.

## Risk and Follow-Up Notes

- `Main` currently implements minimal routing to satisfy BOLT-2.3; full menu action wiring is still pending Unit-03.
- Selftest scenarios intentionally avoid external frameworks (no JUnit) to preserve default-package/JDK-only constraints.
- Future CLI work should preserve existing selftest flag/menu behavior to avoid regressions in `TEST-10`.

## Deletion Assessment

Build artifacts detected and recorded as deletion candidates in changelog:

- `aes256-java/*.class` outputs (including `SelfTest` inner-class artifacts).

Per task rules, no files were deleted in this workflow.

## Next-Step Recommendations

1. Execute `build.task aes256-java/ unit 03 bolt 3.1` to expand `Main` into full menu scaffolding while retaining selftest behavior.
2. Wire encrypt/decrypt menu actions and friendly error mapping in Unit-03 Bolt 3.2.
3. Keep selftest coverage synchronized with future CLI behavior to maintain reliable acceptance evidence.
