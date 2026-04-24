# Version 1.0.0 Documentation

## Title
Stable Public Release and Documentation Reconciliation

## Quick Diagnostic Read

`v1.0.0` promotes the already-validated `v0.3.3` implementation baseline to the first stable public release.

This version:

- bumps the public version markers from `v0.3.3` to `v1.0.0`,
- documents the supported `1.x` CLI and library baseline,
- updates security-support guidance for stable releases, and
- removes remaining ignored/internal-path references from public docs.

## One-Sentence Objective

Ship a clean stable-release documentation checkpoint that matches the current validated Java implementation and exposes only public-facing repository context.

## Scope and Versioning

- Previous documented version: `v0.3.3`
- Current documented version: `v1.0.0`
- Release type: stable documentation reconciliation + version promotion

## File-Level Change Summary

1. `README.md`

- Bumped the visible version and status to stable `v1.0.0`.
- Documented the supported `1.x` baseline for the current CLI and library surfaces.
- Closed the remaining public-release roadmap items.

2. `CHANGELOG.md`

- Added the `v1.0.0` release entry with validation evidence and deletion candidates.
- Updated the repository status line to stable `v1.0.0`.

3. `SECURITY.md`

- Changed the support policy from the latest `0.x` pre-release to the latest stable `1.x` line.
- Marked `0.x` pre-release versions unsupported for security fixes.

4. `THIRD-PARTY-NOTICES.md`

- Rephrased the notice language about local reference license materials so public docs no longer expose ignored/internal paths.

5. Historical version docs

- Sanitized older release notes that still referenced private requirements or local rubric-note paths.

6. `docs/version-1.0.0-docs.md`

- Added this release companion note.

7. Governance docs reviewed

- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `LICENSE.txt` were reviewed and left unchanged because this release does not change contributor behavior requirements or license terms.

## Validation Notes

Validation re-run for this version scope:

- `javac *.java` in `aes256-java/` passed.
- `java Main --help` in `aes256-java/` passed.
- `java Main --selftest` in `aes256-java/` passed with `SELFTEST SUMMARY passed=6 failed=0`.

## Release Readiness Notes

- No Java source changes were required for this release; the stable bump is based on the already-complete implementation and its acceptance evidence.
- The supported public surface for `1.x` is the documented `Main` CLI entrypoints, `SelfTest` runner, `AesGcmEngine` encrypt/decrypt overloads, and the `TextCipher` / `FileCipher` wrappers.
- Public docs no longer enumerate ignored workflow or local note paths.

## Deletion Assessment

Build artifacts listed as deletion candidates in `CHANGELOG.md`:

- `aes256-java/*.class`
- `aes256-java/*$*.class`

Per task rules, no files were deleted in this workflow.

## Next-Step Recommendations

1. Use `v1.0.1` for documentation or security fixes that do not change the public API.
2. Reserve `v1.1.0` for additive CLI or library features that remain backward compatible.
3. Cut a new major version only if the documented `1.x` surface changes incompatibly.
