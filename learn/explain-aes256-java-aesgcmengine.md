# AesGcmEngine.java Explanation Guide

## 1) Title
`aes256-java/AesGcmEngine.java` — AES-256-GCM core engine (key derivation, byte-array envelope, stream envelope)

## 2) Quick Diagnostic Read
You are ready for this file if you can read basic Java methods, exceptions, and byte arrays.

High-value concepts introduced here:
- PBKDF2-HMAC-SHA256 key derivation with fixed parameters.
- AES/GCM envelope formats for both byte arrays and streams.
- Per-record authenticated encryption for streaming files.
- Memory hygiene patterns (`Arrays.fill`) for sensitive data.

## 3) One-Sentence Objective
Understand exactly how `AesGcmEngine` derives keys, formats encrypted envelopes, and safely encrypts/decrypts byte arrays and streams using AES-256-GCM.

## 4) Why This File Matters
This is the cryptographic core used by the wrappers (`TextCipher`, `FileCipher`) and CLI flows. If this file is wrong, every text/file encryption path can fail, silently weaken security, or lose interoperability with already-produced ciphertext envelopes.

## 5) Plan A / Plan B
Plan A (Code-first, 90-120 minutes):
1. Read constants and envelope formats first.
2. Trace `encrypt(byte[], ...)` and `decrypt(byte[], ...)` as one pair.
3. Trace stream `encrypt(...)` and `decrypt(...)` as another pair.
4. Finish with helper methods (`readFully`, `fillRecordIv`, etc.).

Plan B (Concept-first, 60-90 minutes):
1. Learn the two envelope formats (byte-array vs stream).
2. Learn key derivation contract (`deriveKey`).
3. Map each concept back to implementation line ranges.

## 6) System View / Mental Model
```text
passphrase + random salt
  -> PBKDF2WithHmacSHA256 (210,000 iters, 256-bit key)
  -> AES key

Byte-array mode:
  plaintext
    -> random IV (12 bytes)
    -> AES/GCM encrypt
    -> envelope: salt(16) || iv(12) || ciphertext+tag(16)

Stream mode:
  input stream read in <=64 KiB chunks
    -> envelope header: salt(16) || streamIv(12)
    -> for recordIndex = 0..N-1:
       derive record IV from streamIv + recordIndex
       AES/GCM encrypt chunk
       write: plaintextLength(4-byte big-endian) || ciphertext+tag(16)
```

## 7) What This File Is and Where It Fits
- File type: Java source (`final` utility-like class with public APIs).
- Role: cryptographic primitive layer for PHASE-01 behavior.
- Consumers (observed): `TextCipher`, `FileCipher`, `Main`, `SelfTest`.
- External dependencies: JDK crypto (`Cipher`, `SecretKeyFactory`, `GCMParameterSpec`, `PBEKeySpec`) and standard I/O.

## 8) Just-Enough Primer
- `Cipher.getInstance("AES/GCM/NoPadding")` gives authenticated encryption (confidentiality + integrity).
- PBKDF2 turns a passphrase into a fixed-size key; here output is 256 bits.
- `GCMParameterSpec(128, iv)` means 128-bit authentication tag and a supplied IV/nonce.
- `ByteBuffer` is used only to concatenate envelope bytes in deterministic order.

## 9) Whole-File Outline Mapped to Code Regions
- Imports and class-level purpose comments: lines 1-23.
- Security and format constants: lines 25-40.
- `deriveKey(char[], byte[])`: lines 59-85.
- Byte-array encrypt API `encrypt(byte[], char[])`: lines 105-133.
- Byte-array decrypt API `decrypt(byte[], char[])`: lines 153-179.
- Stream encrypt API `encrypt(InputStream, OutputStream, char[])`: lines 194-248.
- Stream decrypt API `decrypt(InputStream, OutputStream, char[])`: lines 265-332.
- Helpers:
  - random bytes generator: lines 334-338.
  - full-read helpers: lines 340-356.
  - length write/read helpers: lines 358-377.
  - per-record IV derivation: lines 379-397.
  - passphrase wipe helper: lines 399-403.

## 10) Walkthrough by Section
### Constants and security knobs (lines 25-40)
This section defines hard constraints used everywhere else: 16-byte salt, 12-byte GCM IV, 128-bit GCM tag, 64 KiB stream chunk size, and max stream records (`2^32`). These constants enforce envelope compatibility and boundary checks. Changing them carelessly breaks decryption compatibility with existing outputs.

### `deriveKey` (lines 59-85)
What it does:
- Validates passphrase and salt.
- Uses PBKDF2-HMAC-SHA256 with 210,000 iterations to derive a 256-bit AES key.
- Returns `SecretKeySpec` for AES.

How it works:
- Null/length checks throw `IllegalArgumentException`.
- For bad salt paths, passphrase is wiped before throw.
- `PBEKeySpec` and temporary key bytes are cleared in `finally`.

Data flow:
- In: passphrase chars + salt bytes.
- Out: `SecretKey` object.

Side effects/contracts:
- Passphrase argument is intentionally zeroed before return.
- Salt must be exactly 16 bytes.

### Byte-array `encrypt` (lines 105-133)
What it does:
- Encrypts plaintext bytes into one envelope: `salt || iv || ciphertext+tag`.

How it works:
- Rejects null plaintext and wipes passphrase.
- Generates random salt and IV.
- Derives key from passphrase+salt.
- Performs AES/GCM encryption with IV.
- Concatenates components using `ByteBuffer`.
- Clears temporary sensitive buffers in `finally`.

What breaks if changed carelessly:
- Envelope ordering or lengths changing will make `decrypt` incompatible.

### Byte-array `decrypt` (lines 153-179)
What it does:
- Parses envelope and authenticates/decrypts ciphertext.

How it works:
- Validates envelope not null and at least minimal size.
- Slices salt/IV/ciphertext+tag by fixed offsets.
- Re-derives key and calls `Cipher#doFinal` for authenticated decrypt.
- Clears working buffers in `finally`.

Side effects:
- Throws `GeneralSecurityException` when auth fails (tamper/wrong passphrase).

### Stream `encrypt` (lines 194-248)
What it does:
- Encrypts input stream as a sequence of AEAD records.

How it works:
- Writes header first: `salt(16)` then `streamIv(12)`.
- Reads up to 64 KiB plaintext per loop.
- For each record: computes record IV from base `streamIv` + record index, encrypts, writes plaintext length (4-byte big-endian), writes ciphertext+tag.
- Flushes output and wipes buffers.

Contracts/invariants:
- Record count must stay below `MAX_STREAM_RECORDS`.
- Record plaintext length is exactly what is written in the 4-byte prefix.

### Stream `decrypt` (lines 265-332)
What it does:
- Reads stream envelope header and decrypts record-by-record.

How it works:
- Reads full header (`salt`, `streamIv`) or fails.
- Repeats:
  - reads 4-byte record length (`-1` means clean end of stream),
  - validates length bounds,
  - reads exact ciphertext+tag length,
  - derives same record IV from `streamIv` + index,
  - decrypts/authenticates one record,
  - checks decrypted chunk length matches announced plaintext length,
  - writes plaintext to output.
- Flushes and wipes buffers.

Why this matters:
- Independent per-record authentication prevents releasing unauthenticated plaintext for that record.

### Helpers (lines 334-403)
- `randomBytes`: central secure randomness source.
- `readFully`: exact-byte read utility for structured binary parsing.
- `writeInt`/`readRecordLength`: deterministic big-endian record length encoding.
- `fillRecordIv`: computes per-record IV by adding `recordIndex` into 12-byte IV with carry and overflow detection.
- `wipePassphrase`: safe null-tolerant passphrase wipe.

## 11) Data Flow / Control Flow / Dependency Map
Data flow summary:
1. Caller provides passphrase + data.
2. Engine derives a per-operation key from passphrase + random salt.
3. Engine encrypts/decrypts with AES/GCM and deterministic envelope layout.
4. Caller gets envelope/plaintext.

Control flow summary (stream mode):
- Header parse/write -> record loop -> per-record IV derivation -> AEAD operation -> length and bounds checks -> flush.

Dependency map:
- Requires: JDK crypto providers supporting `PBKDF2WithHmacSHA256` and `AES/GCM/NoPadding`.
- Required by: `TextCipher` (byte-array mode), `FileCipher` (stream mode), CLI/self-test orchestration.

Safe modification points (low risk):
- Error message wording (keeping exception types and checks intact).
- Additional inline comments or docs.

Dangerous modification points (high risk):
- Constant sizes/iterations.
- Envelope ordering and offsets.
- Record length encoding semantics.
- `fillRecordIv` logic and max-record guard.

## 12) Minimal Usage Example or Execution Trace
Hypothetical execution trace (byte-array mode):
1. Input plaintext: `"hello"` and passphrase `"secret"`.
2. Engine generates random 16-byte salt and 12-byte IV.
3. `deriveKey` computes 256-bit key from passphrase+salt.
4. AES/GCM returns `ciphertext+tag`.
5. Output envelope bytes are returned as `salt || iv || ciphertext+tag`.
6. Decrypt path reverses this process and only returns plaintext if tag verification succeeds.

## 13) Common Pitfalls / Misconceptions
- Misconception: "Any salt length works." Reality: this implementation requires exactly 16 bytes.
- Misconception: "Stream mode uses one IV for all chunks." Reality: each record gets a derived IV from `streamIv + recordIndex`.
- Pitfall: changing chunk size or length encoding without migration strategy breaks compatibility.
- Pitfall: reusing a consumed passphrase from wrappers (`CryptoOperation`) without resetting it.

## 14) Safe-Change Guide
Safe first changes:
1. Add stricter argument validation that preserves existing behavior and formats.
2. Add tests for edge cases (invalid header, truncated record, max record bounds).
3. Improve method documentation.

High-risk changes (need design + test migration):
1. Envelope format changes (`salt||iv||...` or stream record layout).
2. KDF algorithm/iteration/output-size changes.
3. IV derivation strategy changes.
4. Record length endianness or width changes.

## 15) Invariants / Contracts / Side Effects
Observed invariants:
- Byte-array envelope layout is fixed: `salt(16) || iv(12) || ciphertext || tag(16)`.
- Stream envelope layout is fixed: `salt(16) || streamIv(12) || (len(4) || ciphertext || tag(16))*`.
- `deriveKey` expects non-empty passphrase and 16-byte salt.
- Stream record plaintext length must be `1..65536` bytes.
- `recordIndex` must remain `< 2^32`.

Security side effects:
- Passphrase arrays are wiped in multiple code paths.
- Temporary salt/IV/transfer/ciphertext buffers are cleared in `finally` blocks.

Inference (explicit):
- The chosen PBKDF2 iteration count appears to balance coursework performance and brute-force cost, but exact rationale is not documented in this file.

## 16) Self-Check or Practice Drill
Exercise (20-40 min):
- Add tests that verify stream decryption fails for:
  1. header shorter than 28 bytes,
  2. truncated record payload after a valid length prefix,
  3. out-of-range record length (`0` or `> 65536`).

Self-check rubric:
- You pass if each case fails with the expected exception path and existing happy-path tests still pass unchanged.

## 17) Artifact Map
Files read as target/supporting context:
- `aes256-java/AesGcmEngine.java` (target file; full read).
- `aes256-java/TextCipher.java` (consumer mapping to byte-array APIs).
- `aes256-java/FileCipher.java` (consumer mapping to stream APIs).
- `aes256-java/CryptoOperation.java` (passphrase lifecycle contract used by consumers).
- `aes256-java/README.md` (project role/context statements).

## 18) Copy-Paste Prompt Examples
```text
explain aes256-java/AesGcmEngine.java
explain aes256-java/TextCipher.java
explain aes256-java/FileCipher.java
explain aes256-java/CryptoOperation.java
explain aes256-java "stream envelope format"
```

## 19) 24-72 Hour Next Steps
1. Explain one wrapper file next (`TextCipher` or `FileCipher`) and compare error-handling boundaries.
2. Build a small compatibility test that encrypts once and decrypts after process restart.
3. Create a short "format contract" note documenting envelope bytes and record rules for future versioning.

