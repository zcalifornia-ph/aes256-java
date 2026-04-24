# aes256-java Submission README

Group submission: `Adeva_California_Rizal_PE04.zip`

Maintainers:
- Jayrad P. Adeva
- Zildjian E. California
- Rey Marvin C. Riza

## Objective

`aes256-java` is a flat-directory Java 17 submission that demonstrates AES-256-GCM encryption as both:

- an interactive CLI for text and file encryption/decryption, and
- a small embeddable library built around clear OOP concepts.

Educational warning: this project is for learning and coursework only, not production cryptography deployment.

## Files in This Submission

- `AesGcmEngine.java`
- `CryptoOperation.java`
- `FileCipher.java`
- `Main.java`
- `SelfTest.java`
- `TextCipher.java`
- `README.md`

## Compile and Run

From the extracted submission directory:

```powershell
javac *.java
java Main
```

Useful command-line entry points:

```text
java Main --help
java Main --selftest
java Main --selftest-large
java Main
```

## CLI Overview

Interactive menu:

```text
1) Encrypt text
2) Decrypt text
3) Encrypt file
4) Decrypt file
5) Run Smoke Test
6) About
0) Quit
```

Behavior summary:

- Text encryption prints a Base64 envelope.
- Text decryption prints the recovered plaintext or a friendly authenticated-failure message.
- File encryption writes `<input>.enc`.
- File decryption strips a trailing `.enc`; otherwise it writes `<input>.dec`.
- Existing output paths are refused with `refusing to overwrite: <path>`.
- When a real console is available, passphrase input is masked. Otherwise the program warns that passphrase input will be visible.

## OOP Concept Map

Authoritative course definitions come from `oop-notes/java-oop.txt` in the repository source of this project. The concrete implementation anchors in this submission are:

| Concept | Concrete Source Anchor |
|---|---|
| Encapsulation | `CryptoOperation#passphrase` with `getPassphrase()`, `setPassphrase(char[])`, `consumePassphrase()`, `clearStoredPassphrase()` |
| Inheritance | `TextCipher extends CryptoOperation`; `FileCipher extends CryptoOperation` |
| Method Overloading | `TextCipher#encrypt(String)` and `TextCipher#encrypt(char[])`; `FileCipher#encrypt(Path)`, `FileCipher#encrypt(File)`, `FileCipher#encrypt(Path, Path)` |
| Method Overriding | `TextCipher#describe()` and `FileCipher#describe()` override `CryptoOperation#describe()` |

## Library Example

```java
AesGcmEngine engine = new AesGcmEngine();
TextCipher textCipher = new TextCipher(engine, "secret".toCharArray());
String ciphertext = textCipher.encrypt("hello");

textCipher.setPassphrase("secret".toCharArray());
String recovered = textCipher.decrypt(ciphertext);

FileCipher fileCipher = new FileCipher(new AesGcmEngine(), "secret".toCharArray());
java.nio.file.Path encrypted = fileCipher.encrypt(java.nio.file.Path.of("notes.txt"));
```

## What This Submission Demonstrates

- AES-256/GCM authenticated encryption with PBKDF2-HMAC-SHA256 key derivation.
- Byte-array and streaming encryption/decryption flows.
- Friendly user-path errors for wrong passphrase, tamper/corruption, missing file, and overwrite refusal.
- Self-validation through `SelfTest`, available from both `--selftest` and menu option `5`.
- OOP concepts demonstrated directly in the source code and described in this README.

## Quick Verification

After `javac *.java`, these checks should work:

```powershell
java Main --selftest
```

Expected summary:

```text
SELFTEST SUMMARY passed=6 failed=0
```
