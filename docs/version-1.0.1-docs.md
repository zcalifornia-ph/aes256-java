# Version 1.0.1 Documentation

## Title
Public Documentation Terminology Cleanup and Release-Note Reconciliation

## Quick Diagnostic Read

`v1.0.1` is a documentation-only patch release.

This update focuses on one scope:

- remove internal workflow/task-system terminology from public-facing docs and learning guides,
- keep documentation accurate and readable without changing runtime behavior,
- preserve repository policy docs and license content unless changes are required.

## One-Sentence Objective

Ship a clean, public documentation surface that no longer references internal workflow mechanics while preserving the same `1.x` runtime/API baseline.

## Scope and Versioning

- Previous documented version: `v1.0.0`
- Current documented version: `v1.0.1`
- Release type: stable patch (`docs-only`)

## File-Level Change Summary

1. Root docs updated

- `README.md`
  - bumped version/status from `v1.0.0` to `v1.0.1`,
  - added roadmap line for the `v1.0.1` documentation cleanup release.
- `CHANGELOG.md`
  - added `v1.0.1` entry with full scope and validation notes,
  - updated changelog status marker to `stable (v1.0.1)`.

2. Versioned docs reconciled

The following files were updated to remove internal workflow terms and hidden-path references while keeping historical release intent intact:

- `docs/version-0.0.3-docs.md`
- `docs/version-0.0.4-docs.md`
- `docs/version-0.1.0-docs.md`
- `docs/version-0.1.1-docs.md`
- `docs/version-0.1.2-docs.md`
- `docs/version-0.1.3-docs.md`
- `docs/version-0.2.0-docs.md`
- `docs/version-0.2.1-docs.md`
- `docs/version-0.2.2-docs.md`
- `docs/version-0.3.0-docs.md`
- `docs/version-0.3.1-docs.md`
- `docs/version-0.3.2-docs.md`
- `docs/version-0.3.3-docs.md`
- `docs/version-1.0.0-docs.md`

3. Learning artifacts reconciled

All `learn/explain-aes256-java-*.md` artifacts were reviewed and updated so they no longer mention internal task files or hidden workflow surfaces, while preserving the instructional content for each Java source file.

## Validation Summary

Documentation scans were re-run after edits to confirm removal of internal-workflow markers in the requested scope.

Markers checked and removed:

- internal task/workflow labels,
- hidden-path references tied to ignored internal files.

Result:

- No remaining matches in the requested docs scope.
- No source-code or runtime API changes were introduced.

## Governance Docs Review

Reviewed and left unchanged:

- `CODE_OF_CONDUCT.md`
- `CONTRIBUTING.md`
- `SECURITY.md`
- `LICENSE.txt`
- `THIRD-PARTY-NOTICES.md`

Reason: no policy, legal, or security-process changes were required for this release.

## Deletion Assessment

Build artifacts listed as deletion candidates in `CHANGELOG.md`:

- `aes256-java/*.class`
- `aes256-java/*$*.class`

Per project policy, no files were deleted in this release.

## Risk and Compatibility Notes

- Compatibility risk: low.
- Runtime risk: none introduced by this change scope.
- Residual risk: historical release-note phrasing was normalized for public readability, so future edits should preserve factual release chronology when refining wording.

## Next-Step Recommendations

1. Keep future release docs free of hidden workflow/path references during authoring, not only during cleanup passes.
2. Apply the same public-facing terminology rule to any new learning docs added under `learn/`.
3. Continue stable `1.x` maintenance with runtime/API changes documented separately from docs-only patches.
