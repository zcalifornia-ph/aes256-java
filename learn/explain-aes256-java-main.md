# Title
`Main.java` Explained: Interactive AES-256-GCM CLI Entrypoint

## Quick Diagnostic Read
You are ready for this guide if you can read basic Java control flow (`if`, `switch`, `try/catch/finally`) and understand what command-line arguments are.

High-value new ideas in this file:
- separation between CLI orchestration and crypto implementation,
- defensive input handling for interactive menus,
- passphrase hygiene patterns (clear after use),
- user-facing error mapping for cryptographic and file operations.

## One-Sentence Objective
Understand how `aes256-java/Main.java` routes CLI inputs into `TextCipher`, `FileCipher`, and `SelfTest` while enforcing safe input flow and clear error handling.

## Why This File Matters
`Main.java` is the only interactive entrypoint for this project. It decides which operation runs, how input is collected, and what the user sees on success or failure. If this file is wrong, the crypto engine can still be correct but effectively unusable or misleading to users.

## Plan A / Plan B
Plan A (code-first, 60-90 min):
1. Read `main(...)` and `runMenu()` first.
2. Read each action handler (`encryptText`, `decryptText`, `encryptFile`, `decryptFile`).
3. Read input helpers and cleanup helpers (`readPassphrase`, `wipe`, `parsePath`).

Plan B (behavior-first, 45-75 min):
1. Read help text and menu output methods first.
2. Simulate one text encrypt and one file decrypt path mentally.
3. Confirm where each exception type is transformed into user-friendly output.

## System View / Mental Model
```text
CLI args
  -> main(String[] args)
     -> --help             -> printHelp() -> exit
     -> --selftest*        -> SelfTest.run(...) -> System.exit(code)
     -> unknown args       -> usage + exit
     -> no args            -> runMenu()

runMenu() loop
  -> printMenu()
  -> read choice
  -> action screen
      1 encryptText()  -> TextCipher(AesGcmEngine)
      2 decryptText()  -> TextCipher(AesGcmEngine)
      3 encryptFile()  -> FileCipher(AesGcmEngine)
      4 decryptFile()  -> FileCipher(AesGcmEngine)
      5 SelfTest.runDefault(...)
      6 printAboutBody()
      0 exit
```

## What This File Is and Where It Fits
- File type: Java source file, default package, final class with static methods.
- Role: interactive CLI controller and presentation layer.
- Direct dependencies used in code: `TextCipher`, `FileCipher`, `AesGcmEngine`, `SelfTest`, standard Java I/O/paths/security APIs.
- Consumers: JVM process launched by `java Main ...`.
- Dependency boundary: `Main` never implements encryption primitives itself; it delegates to cipher classes.

## Just-Enough Primer
- `try (...) {}` with `Scanner` auto-closes the input resource at block end.
- `switch (choice)` handles menu commands as string cases.
- `try/catch/finally` is used to map exceptions to user messages and to wipe secret data in `finally`.
- `char[]` is used for passphrases so values can be overwritten after use (`Arrays.fill(...)`).

## Whole-File Outline Mapped to Code Regions
- Imports and documentation: lines 1-35
- CLI constants and constructor guard: lines 38-70
- Program entrypoint and argument routing: lines 72-99
- Interactive menu loop and dispatch: lines 101-173
- UI rendering (`printMenu`, header/help/about): lines 175-226
- Console encoding and clear-screen behavior: lines 228-280
- Pause/input helpers: lines 282-419
- Action handlers (text/file encrypt/decrypt): lines 291-403
- Passphrase and utility helpers (`readPassphrase`, `parsePath`, `wipe`, `hasArg`): lines 421-462
- Local control-flow exception type: line 464

## Walkthrough by Section
1. Entrypoint argument routing (`main`, lines 77-99)
- What it does: normalizes output encoding, then routes startup to help, selftest, usage error, or menu mode.
- How it works: checks flags with `hasArg(...)`; `--selftest` and `--selftest-large` call `SelfTest.run(...)` then terminate with `System.exit(code)`.
- Data flow: `args` in, chosen mode out.
- Side effects: prints to stdout/stderr and may exit process.
- Contract: unknown arguments are rejected with usage text instead of being ignored.

2. Persistent menu loop (`runMenu`, lines 101-173)
- What it does: keeps the program in an interactive loop until quit or input closure.
- How it works: reads one line, dispatches by switch case, catches `InputClosedException` to exit cleanly.
- Data flow: user text input -> action method -> console output.
- Side effects: screen clearing, stdout prints, calls crypto operations.
- Invariant: after menu actions 1-6, flow returns to menu only after `promptEnterToContinue(...)`.

3. Rendering and action screens (`printMenu`, `printHelp`, `printAboutBody`, `printCliHeader`, `beginActionScreen`, lines 175-226)
- What it does: centralizes visible UI strings and presentation formatting.
- Why it exists: keeps command logic separate from printing details (inference).
- Side effects: stdout output only.
- Risk point: changing menu numbering without updating switch cases breaks behavior consistency.

4. Console setup and clearing (`configureConsoleEncoding`, `detectOutputCharset`, `clearConsole`, `tryNativeClear`, lines 228-280)
- What it does: sets output streams to detected charset and clears screen with OS-aware fallback.
- How it works:
  - charset: console charset when available, otherwise UTF-8,
  - clear: native `cls`/`clear` attempt, then ANSI sequence, then blank-line fallback.
- Side effects: replaces `System.out` and `System.err`; spawns subprocess for clear command.
- Invariant: clear behavior should never crash the app; failures degrade gracefully.

5. Text crypto actions (`encryptText`, `decryptText`, lines 291-336)
- What it does: prompt text/ciphertext + passphrase, then call `TextCipher`.
- How it works: constructs `TextCipher(new AesGcmEngine(), passphrase)` per operation.
- Error mapping:
  - text decrypt AEAD tag failure -> "wrong passphrase or corrupted ciphertext",
  - generic crypto failure -> generic cryptographic error message,
  - validation errors -> explicit message from `IllegalArgumentException`.
- Security side effects: always attempts to clear cipher-stored passphrase and local `char[]` in `finally`.

6. File crypto actions (`encryptFile`, `decryptFile`, lines 338-403)
- What it does: prompt file path + passphrase, then call `FileCipher`.
- How it works: parse path with `Paths.get(...).normalize()` and run encrypt/decrypt on that path.
- Error mapping:
  - invalid path syntax,
  - overwrite refusal (`FileAlreadyExistsException`),
  - missing file/IO/validation,
  - decrypt auth failure (`AEADBadTagException`).
- Invariant: passphrase cleanup logic mirrors text flow.

7. Input and secret handling helpers (`promptLine`, `promptRequiredLine`, `readPassphrase`, `parsePath`, `wipe`, `hasArg`, lines 405-462)
- What they do:
  - `promptLine` enforces input availability,
  - `promptRequiredLine` enforces non-empty values,
  - `readPassphrase` hides typing when console is attached; warns if fallback input is visible,
  - `wipe` clears char arrays,
  - `hasArg` performs exact flag match.
- Contract: empty passphrases are rejected in both console and fallback modes.

8. Internal control signal (`InputClosedException`, line 464)
- What it does: lightweight runtime exception used for early exits when input stream closes mid-flow.
- Why it exists: avoids repeating return/error plumbing through every helper (inference).

## Data Flow / Control Flow / Dependency Map
- Startup control flow:
  - `main` -> mode selection -> either immediate exit path or `runMenu` loop.
- Interactive data flow:
  - user input (`Scanner`) -> validation helpers -> cipher invocation -> console response.
- Dependency map:
  - `Main` -> `SelfTest` for built-in acceptance checks,
  - `Main` -> `TextCipher` and `FileCipher` for operations,
  - `TextCipher`/`FileCipher` -> `AesGcmEngine` for cryptographic primitives,
  - `TextCipher`/`FileCipher` inherit passphrase lifecycle behavior from `CryptoOperation`.

## Minimal Usage Example or Execution Trace
Hypothetical trace:
1. User runs `java Main` with no args.
2. `main` calls `runMenu`.
3. User enters `1`.
4. `encryptText` asks for plaintext and passphrase.
5. `TextCipher.encrypt(...)` returns Base64 envelope.
6. CLI prints ciphertext, pauses, and returns to menu.

Hypothetical selftest trace:
1. User runs `java Main --selftest`.
2. `main` routes to `SelfTest.run(System.out, false)`.
3. Program exits with `0` if all tests pass, otherwise non-zero.

## Common Pitfalls / Misconceptions
- "`parsePath().normalize()` validates existence": false. Existence and readability checks happen in `FileCipher`.
- "`System.console()` is always available": false. IDEs and redirected input often return `null`.
- "Catching `GeneralSecurityException` is enough": not for friendly UX; specific catches (`AEADBadTagException`) provide clearer guidance.
- "Passphrase is fully safe just because `char[]` is used": partially true. This file wipes arrays, but full memory-safety guarantees are not possible in managed runtimes (inference).

## Safe-Change Guide
Lower-risk changes:
- Help/about/menu text formatting.
- Adding a new non-breaking CLI option with clear branch in `main`.
- Adjusting user-facing error messages without changing exception classes caught.

Higher-risk changes:
- Modifying `finally` blocks in crypto action handlers; this can regress passphrase cleanup.
- Altering menu-number dispatch without synchronized UI updates.
- Removing specific exception catches (`AEADBadTagException`, `FileAlreadyExistsException`) and collapsing into generic errors.

## Invariants / Contracts / Side Effects
- Invariant: passphrase arrays are wiped after each operation attempt (`wipe` and cipher cleanup in `finally`).
- Invariant: empty required fields (path/passphrase) are rejected before crypto calls.
- Contract: `--selftest*` modes terminate process with explicit exit code.
- Side effect: `configureConsoleEncoding` replaces global `System.out` and `System.err` streams.
- Side effect: clear-screen logic may spawn a native process (`cmd /c cls` or `clear`).

## Self-Check or Practice Drill
Exercise (20-40 min): add a new menu action `7) Print runtime diagnostics` that shows whether `System.console()` is attached and the detected output charset, without changing crypto behavior.

Self-check rubric:
- Menu displays option 7 and dispatches correctly.
- Existing options 0-6 still behave the same.
- New action does not throw when stdin is redirected.
- No passphrase cleanup logic was removed or weakened.

## Artifact Map
Target file read in full:
- `aes256-java/Main.java` - file being explained.

Supporting context loaded for truthful dependency explanation:
- `aes256-java/TextCipher.java` - text encryption/decryption facade called by `Main`.
- `aes256-java/FileCipher.java` - file encryption/decryption facade called by `Main`.
- `aes256-java/AesGcmEngine.java` - crypto engine instantiated by `Main` via ciphers.
- `aes256-java/SelfTest.java` - selftest runner invoked by CLI flags and menu item.
- `aes256-java/CryptoOperation.java` - passphrase lifecycle contract used by both cipher facades.

## Copy-Paste Prompt Examples
- `explain aes256-java/Main.java`
- `explain aes256-java/TextCipher.java`
- `explain aes256-java/FileCipher.java`
- `explain aes256-java/SelfTest.java`
- `explain aes256-java "menu loop and error mapping"`

## 24-72 Hour Next Steps
1. Explain one dependency next: `TextCipher.java`.
2. Trace one end-to-end path: menu choice `3` into `FileCipher.encrypt(Path)` and resulting output naming policy.
3. Run `--selftest` and map each test ID to the class/method it validates.
4. Optional hardening exercise: design a non-interactive mode (`--encrypt-text`) while preserving current interactive behavior.
