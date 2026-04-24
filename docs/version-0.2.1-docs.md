# Version 0.2.1 Documentation

## Title
Unit 03 / Bolt 3.1: Interactive CLI Menu Scaffolding and Help/About Routing

## Quick Diagnostic Read

`v0.2.1` is a code-and-docs milestone that completes Unit-03 Bolt-3.1 by converting `Main` from a minimal selftest hook into a stable CLI shell.

This version adds:

- command dispatch for `--help`, `--selftest`, and `--selftest-large`,
- a resilient interactive menu loop with deterministic option behavior,
- updated root docs reflecting the new CLI baseline and next-bolt boundary.

## One-Sentence Objective

Ship a stable, documented CLI scaffold that preserves selftest behavior and prepares cleanly for Bolt-3.2 encrypt/decrypt action wiring.

## Scope and Versioning

- Previous documented version: `v0.2.0`
- Current documented version: `v0.2.1`
- Release type: pre-alpha patch milestone for Unit-03 Bolt-3.1 implementation

## File-Level Change Summary

1. `aes256-java/Main.java`

- Added OOP concept map header comment anchors at file top.
- Added explicit `--help` handling and usage output.
- Preserved/validated `--selftest` and `--selftest-large` dispatch.
- Added unknown-argument handling with guidance to `--help`.
- Reworked menu loop to:
  - keep deterministic options `1`..`6` and `0`,
  - route `5` to `SelfTest`,
  - route `6` to About output,
  - keep `1`..`4` as explicit staged placeholders for Bolt-3.2,
  - exit cleanly on EOF and user quit.

2. `README.md`

- Bumped visible version/status references from `v0.2.0` to `v0.2.1`.
- Updated feature list to include Unit-03 menu scaffolding capabilities.
- Updated CLI usage section with `--help` and option `6` behavior.
- Updated roadmap to mark `v0.2.1` complete and track `v0.2.2` as the next CLI wiring milestone.

3. `CHANGELOG.md`

- Bumped status line to `v0.2.1`.
- Added a `v0.2.1` entry summarizing implementation, validation evidence, and deletion candidates.

4. `docs/version-0.2.1-docs.md`

- Added this detailed companion note for the milestone.

5. Governance docs reviewed

- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` were reviewed and left unchanged because no governance-policy change was required by this scope.

## Validation Notes

Validation executed for this version scope:

- Compile gate:
  - `javac *.java` in `aes256-java/` passed.
- Help route:
  - `java Main --help` exit code `0`, usage/options plus educational warning printed.
- Selftest route:
  - `java Main --selftest` exit code `0`, summary `SELFTEST SUMMARY passed=6 failed=0`.
- Menu-path selftest:
  - scripted input (`5`, then `0`) to `java Main` executed SelfTest, printed `selftest exit code=0`, and exited cleanly.
- Menu-path quit:
  - scripted input (`0`) to `java Main` exited cleanly with `bye`.

## Traceability and Artifact Scope Notes

- Build-task artifacts for Unit-03 were updated under `aes256-java/ai-dlc-docs/` during implementation, including design, ADR, traceability, and requirements updates.
- Those paths are intentionally ignored by this repository’s `.gitignore`, so commit scope for `commit-p.task` includes only tracked files in the logical public/documentation surface plus source code changes.

## Deletion Assessment

Build artifacts detected and recorded as deletion candidates in changelog:

- `aes256-java/*.class`
- `aes256-java/*$*.class`

Per task rules, no files were deleted in this workflow.

## Next-Step Recommendations

1. Execute Unit-03 / Bolt-3.2 to wire options `1`..`4` to `TextCipher`/`FileCipher` with friendly error mapping.
2. Preserve current selftest/help/about behavior while adding encrypt/decrypt handlers.
3. Keep versioned docs updates synchronized with each Bolt completion to maintain release traceability.
