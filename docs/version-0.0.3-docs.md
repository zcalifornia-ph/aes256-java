# Version 0.0.3 Documentation

## Title
PHASE 01 / MILESTONE 1.1 Completion: PBKDF2 Key-Derivation Baseline (aes256-java)

## Quick Diagnostic Read

This version introduces the first executable crypto implementation artifact:

- `AesGcmEngine.java` now contains a deterministic PBKDF2 key-derivation seam for PHASE 01 / MILESTONE 1.1.
- Root docs are updated to reflect the implemented baseline and current scope.
- The change is intentionally narrow: key derivation only, with encrypt/decrypt APIs deferred to upcoming milestones.

## One-Sentence Objective

Land a secure, deterministic, JDK-only key-derivation baseline that unblocks subsequent AES-GCM byte-array and streaming milestones.

## Why This Version Matters

Before this update:

- repository docs described crypto intent but no Java implementation was committed,
- no stable key-derivation contract existed for future encryption/decryption paths,
- release metadata still reflected `v0.0.2`.

After this update:

- the repo contains a concrete Java crypto baseline in `aes256-java/AesGcmEngine.java`,
- KDF parameters are fixed and enforced in code,
- README and CHANGELOG are aligned to `v0.0.3`.

## Scope and Versioning

- Previous documented version: `v0.0.2`
- Current documented version: `v0.0.3`
- Release type: feature baseline + documentation update

## File-Level Change Summary

1. `aes256-java/AesGcmEngine.java`

- Added core class with MILESTONE 1.1 KDF constants:
  - `PBKDF2_ITERATIONS = 210000`
  - `SALT_LENGTH_BYTES = 16`
  - `DERIVED_KEY_BITS = 256`
- Implemented `deriveKey(char[] passphrase, byte[] salt)` with:
  - null/empty input validation,
  - deterministic PBKDF2 derivation via `SecretKeyFactory`,
  - explicit cleanup (`PBEKeySpec.clearPassword()`, passphrase zeroing, temporary key-byte zeroing).

2. `README.md`

- Bumped visible version and status to `v0.0.3`.
- Added practical quick-start compile step for the current baseline.
- Reframed usage notes to distinguish implemented baseline from planned CLI/API work.
- Updated roadmap wording to mark MILESTONE-1.1 baseline completion.

3. `CHANGELOG.md`

- Added `v0.0.3` entry with implementation and documentation deltas.
- Updated top status marker to `v0.0.3`.

## Verification Notes

Validation performed for this version:

- `javac AesGcmEngine.java` succeeds.
- Deterministic KDF smoke evidence (same passphrase + same salt => same derived key) was captured during the milestone run.

## Deletion Assessment

No tracked build artifacts were introduced in this version context.
No deletion candidates were identified for commit scope.

## Compatibility and Risk Notes

- Runtime risk: low-to-moderate; this is the first crypto code path and should remain under milestone-level validation in follow-up iterations.
- Scope risk: encrypt/decrypt methods are not implemented yet and must land in MILESTONE-1.2/MILESTONE-1.3.
- Repo hygiene note: internal planning artifacts are ignored by `.gitignore`, so they are preserved locally but not part of this tracked commit.

## Next-Step Recommendations

1. Execute `build` for `PHASE-01` `MILESTONE-1.2` to add byte-array AES-GCM encrypt/decrypt APIs.
2. Add a committed selftest harness once MILESTONE-1.2 is active so deterministic KDF and round-trip checks run from source in-repo.
3. Keep README usage snippets synchronized with each milestone completion to avoid drift between docs and implementation.


