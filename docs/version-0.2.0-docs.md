# Version 0.2.0 Documentation

## Title
Documentation Reconciliation and Public Version Bump to 0.2.0

## Quick Diagnostic Read

`v0.2.0` is a documentation-focused milestone that reconciles public docs with current code reality after Unit-02 selftest integration.

This update keeps implementation behavior unchanged and focuses on:

- docs/task alignment of root docs,
- public version bump from `v0.1.3` to `v0.2.0`,
- cleanup of changelog deletion notes to avoid exposing ignored-path details.

## One-Sentence Objective

Ship a clean documentation checkpoint at `v0.2.0` that accurately reflects the current baseline (`Main` + `SelfTest` entry hooks) and keeps public docs free of ignored/internal path leakage.

## Scope and Versioning

- Previous documented version: `v0.1.3`
- Current documented version: `v0.2.0`
- Release type: documentation reconciliation + milestone version bump

## File-Level Change Summary

1. `README.md`

- Bumped visible version/status to `v0.2.0`.
- Updated CLI-mode section to distinguish:
  - current available selftest entrypoints,
  - planned encrypt/decrypt CLI action shape.
- Updated roadmap to mark `v0.2.0` and keep remaining CLI work explicit.

2. `CHANGELOG.md`

- Bumped changelog status line to `v0.2.0`.
- Added `v0.2.0` entry summarizing docs reconciliation and validation state.
- Replaced explicit ignored artifact-path listings in prior deletion notes with generic wording.

3. `docs/version-0.2.0-docs.md`

- Added this release note as the detailed companion for the milestone.

4. Governance docs reviewed

- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` were reviewed and left unchanged because no policy-level changes were required by this scope.

## Validation Notes

Validation re-run for docs fidelity:

- `javac *.java` in `aes256-java/` passed.
- `java Main --selftest` passed with `SELFTEST SUMMARY passed=6 failed=0`.
- Menu-path selftest invocation (`5`, then `0`) passed with `selftest exit code=0`.

## docs.task Reconciliation Notes

- `docs/` was reviewed and remains consistent with current visible project state.
- No `learn/` directory was present in this workspace.
- No root `REQUIREMENTS.md` file was present in this workspace.
- Root public docs were updated only where necessary to match current code behavior and versioning.

## Deletion Assessment

- Local Java compile outputs are still generated during validation.
- These were documented generically in changelog deletion notes and not deleted in this workflow.

## Next-Step Recommendations

1. Execute Unit-03 Bolts to implement full interactive encrypt/decrypt menu behavior and friendly error mapping.
2. Keep selftest output and route behavior stable while expanding `Main`.
3. Continue versioned docs entries per milestone to preserve change traceability.
