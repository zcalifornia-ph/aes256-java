<!-- Adapted from Best-README-Template. Reference-style links live at the bottom of this file. -->
<a id="readme-top"></a>



<!-- PROJECT SHIELDS -->
<div align="center">

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

</div>

<!-- ABOUT THE PROJECT -->
##
[![aes256-java Screen Shot][product-screenshot]](https://github.com/zcalifornia-ph/aes256-java)
##



<div align="center">
<h3 align="center">aes256-java</h3>

  <p align="center">
    <strong>Lightweight, zero-dependency AES-256 encryption for Java. Built with clean OOP design; encapsulation, inheritance, overloading, and overriding.</strong>
    <br />
    Version: v0.3.1
    <br />
    Status: pre-alpha (core AES-GCM APIs, Unit 02 OOP wrappers, Unit 03 interactive CLI wiring, and Unit 04 / Bolt 4.1 documentation hardening are landed; packaging remains next).
    <br />
    <a href="https://github.com/zcalifornia-ph/aes256-java"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/zcalifornia-ph/aes256-java">View Demo</a>
    &middot;
    <a href="https://github.com/zcalifornia-ph/aes256-java/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/zcalifornia-ph/aes256-java/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
## Table of Contents

1. [About The Project](#about-the-project)
   - [Features](#features)
   - [OOP Concept Map](#oop-concept-map)
   - [What aes256-java Is Not](#what-aes256-java-is-not)
   - [Built With](#built-with)
2. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Quick Start](#quick-start)
3. [Usage](#usage)
   - [CLI Mode](#cli-mode)
   - [Library Mode](#library-mode)
4. [Security Notes](#security-notes)
5. [Roadmap](#roadmap)
6. [Contributing](#contributing)
7. [License](#license)
8. [Third-Party Notices](THIRD-PARTY-NOTICES.md)
9. [Contact](#contact)
10. [Acknowledgments](#acknowledgments)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## About The Project

aes256-java is a lightweight, zero-dependency AES-256 encryption toolkit for Java. It ships as both a command-line tool for encrypting plaintext and files, and as an embeddable library you can drop into your own Java projects. The codebase is built with clean object-oriented design, demonstrating encapsulation, inheritance, method overloading, and method overriding.

### Features

- PBKDF2-HMAC-SHA256 key-derivation baseline (`AesGcmEngine`, Bolt 1.1).
- Byte-array AES-256/GCM encrypt/decrypt API (`AesGcmEngine`, Bolt 1.2).
- Stream-based AES-256/GCM encrypt/decrypt API (`AesGcmEngine`, Bolt 1.3).
- OOP abstraction layer with implemented wrappers (`CryptoOperation`, `TextCipher`, `FileCipher`, Bolts 2.1 and 2.2).
- In-program selftest runner (`SelfTest`) reachable via `java Main --selftest` and menu option `5` (Bolt 2.3).
- Fully wired interactive CLI in `Main` for text/file encrypt/decrypt flows with friendly error mapping (Unit 03 / Bolt 3.2).
- Unit 04 / Bolt 4.1 docs hardening: synchronized OOP concept map in `README.md` and `Main.java` with public-member Javadoc coverage updates.
- AES-256 encryption and decryption for plaintext input.
- AES-256 encryption and decryption for files.
- Dual-mode usage: standalone CLI or embeddable library.
- Zero external dependencies; pure Java on top of `javax.crypto`.
- Clean OOP architecture suitable for learning, extending, or integrating.

### OOP Concept Map

Reference notes:
[`java-oop.txt`](aes256-java/oop-notes/java-oop.txt)  
Mirror in source:
[`Main.java`](aes256-java/Main.java) header comment

| Concept | Concrete Source Anchor |
|---|---|
| Encapsulation | `CryptoOperation#passphrase` with `getPassphrase()`, `setPassphrase(char[])`, `consumePassphrase()`, `clearStoredPassphrase()` |
| Inheritance | `TextCipher extends CryptoOperation`; `FileCipher extends CryptoOperation` |
| Method Overloading | `TextCipher#encrypt(String)` and `TextCipher#encrypt(char[])`; `FileCipher#encrypt(Path)`, `FileCipher#encrypt(File)`, `FileCipher#encrypt(Path, Path)` |
| Method Overriding | `TextCipher#describe()` and `FileCipher#describe()` override `CryptoOperation#describe()` |

### What aes256-java Is Not

- Not a vetted production cryptography library. See [Security Notes](#security-notes).
- Not a key-management or secrets-storage system.
- Not a replacement for authenticated encryption libraries such as Tink or Bouncy Castle in regulated or high-assurance contexts.

### Built With

* [![Java][Java]][Java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Status: pre-alpha (v0.3.1). Interfaces and command shapes may change before the first stable release.

### Prerequisites

- A Java Development Kit (JDK 17 or later recommended; any modern JDK that provides `javax.crypto` should work).
- Ability to run `java` and `javac` from your shell.

Verify your toolchain:

```sh
java -version
javac -version
```

### Quick Start

1. Clone the repo.
   ```sh
   git clone https://github.com/zcalifornia-ph/aes256-java.git
   cd aes256-java
   ```
2. Review the project layout and entry points under the project root.
3. Compile the current baseline:
   ```sh
   cd aes256-java
   javac *.java
   ```
4. Start the interactive CLI:
   ```sh
   java Main
   ```
5. Run the in-program assertions:
   ```sh
   java Main --selftest
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

The project exposes two intended usage modes. Concrete command and API signatures are being introduced incrementally as Bolts land.

### CLI Mode

Current available entry points:

```text
java Main --help
java Main --selftest
java Main --selftest-large
java Main
  -> option 1 encrypts text (Base64 envelope output)
  -> option 2 decrypts text (friendly wrong-passphrase/corruption mapping)
  -> option 3 encrypts file (<input>.enc)
  -> option 4 decrypts file (strip .enc else .dec)
  -> option 5 runs SelfTest
  -> option 6 prints About
```

Sample interactive transcript (non-console fallback mode):

```text
=== aes256-java ===
1) Encrypt text
2) Decrypt text
3) Encrypt file
4) Decrypt file
5) SelfTest
6) About
0) Quit
Select option: 1
Plaintext: hello
warning: console is not attached; passphrase input will be visible.
Passphrase: secret
ciphertext (Base64):
<base64-envelope>

Select option: 2
Ciphertext (Base64): <base64-envelope>
warning: console is not attached; passphrase input will be visible.
Passphrase: wrong
decrypt text failed: wrong passphrase or corrupted ciphertext.
```

### Library Mode

Current baseline (`v0.3.1`):

```java
// Current implemented primitives in aes256-java/AesGcmEngine.java:
// - PBKDF2WithHmacSHA256 key derivation (210000 iterations, 16-byte salt, 256-bit key)
// - AES/GCM/NoPadding byte-array encrypt/decrypt with envelope:
//   salt(16) || iv(12) || ciphertext || tag(16)
// - stream encrypt/decrypt overloads for InputStream/OutputStream paths using:
//   salt(16) || streamIv(12) || record(length(4) || ciphertext || tag(16))*
// Unit-02 OOP wrappers are implemented:
// - CryptoOperation abstract base with consume-and-clear passphrase flow
// - TextCipher: Base64 text envelope encrypt/decrypt wrappers
// - FileCipher: stream file encrypt/decrypt wrappers with .enc/.dec naming policy
AesGcmEngine engine = new AesGcmEngine();
char[] passphrase = "secret".toCharArray();
byte[] envelope = engine.encrypt(plaintext, passphrase);
byte[] recovered = engine.decrypt(envelope, "secret".toCharArray());
engine.encrypt(inputStream, encryptedOutputStream, passphrase);
engine.decrypt(encryptedInputStream, decryptedOutputStream, "secret".toCharArray());
TextCipher textCipher = new TextCipher(engine, "secret".toCharArray());
String ciphertext = textCipher.encrypt("hello");
textCipher.setPassphrase("secret".toCharArray()); // reset because passphrase is consumed per operation
String recoveredText = textCipher.decrypt(ciphertext);
String label = textCipher.banner();

// In-program assertions:
int selfTestExit = SelfTest.runDefault(System.out); // 0 when all checks pass
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## Security Notes

This is an educational implementation built as a learning exercise in applied cryptography and object-oriented design. For production systems, prefer:

- Java's built-in `javax.crypto` primitives paired with a vetted key derivation function such as PBKDF2 or Argon2.
- Authenticated encryption modes (for example AES-GCM) from a reviewed cryptographic library.
- Proper key storage (OS keychain, KMS, HSM) rather than passwords embedded in source or configuration.

Do not use this library to protect information with real regulatory, legal, financial, or safety consequences without independent security review.

Report suspected vulnerabilities through the process in [SECURITY.md](SECURITY.md). Do not open public issues for security reports.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] v0.1.0 - Core crypto engine baseline (KDF + byte-array + stream API surfaces).
- [x] v0.1.1 - OOP abstraction hierarchy skeleton (Unit 02 / Bolt 2.1).
- [x] v0.1.2 - OOP behavior wrappers implemented (Unit 02 / Bolt 2.2).
- [x] v0.1.3 - In-program selftest integration with dual entry paths (Unit 02 / Bolt 2.3).
- [x] v0.2.0 - CLI entrypoint with selftest flags/menu routing (`Main` + `SelfTest`).
- [x] v0.2.1 - Unit 03 / Bolt 3.1 menu scaffolding (`--help`, About, resilient menu loop, and staged handlers).
- [x] v0.2.2 - Full interactive encrypt/decrypt CLI wiring and friendly error mapping (Unit 03 / Bolt 3.2).
- [x] v0.3.0 - Documentation reconciliation (`docs.task`) and release metadata alignment.
- [x] v0.3.1 - Unit 04 / Bolt 4.1 docs hardening (Javadoc pass + OOP concept-map synchronization in `README.md` and `Main.java`).
- [ ] v0.3.2 - Library packaging guidance, sample projects, and API stabilization.
- [ ] v1.0.0 - Public stable release with documented API and acceptance tests.

See the [open issues](https://github.com/zcalifornia-ph/aes256-java/issues) for proposed features and known gaps.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are welcome, especially around OOP clarity, test coverage, and documentation for learners.
See [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md) for process, behavior, and vulnerability reporting.

1. Fork the project.
2. Create your feature branch (`git checkout -b feat/your-feature`).
3. Commit your changes (`git commit -m 'feat: add some feature'`).
4. Push to your branch (`git push origin feat/your-feature`).
5. Open a pull request.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/zcalifornia-ph/aes256-java/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=zcalifornia-ph/aes256-java" alt="contrib.rocks image" />
</a>



<!-- LICENSE -->
## License

This project is licensed under the Apache License 2.0.
See [LICENSE.txt](LICENSE.txt) for the full license text and
[THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md) for third-party and adaptation
notices.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Maintainers:

- Jayrad P. Adeva - [@Shinranation](https://github.com/Shinranation) - jpadeva@up.edu.ph
- Zildjian E. California - [@zcalifornia-ph](https://github.com/zcalifornia-ph) - zecalifornia@up.edu.ph
- Rey Marvin C. Rizal - [@marverickdev](https://github.com/marverickdev) - rcrizal@up.edu.ph

Project Link: [https://github.com/zcalifornia-ph/aes256-java](https://github.com/zcalifornia-ph/aes256-java)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

- Built by Adeva, California, and Rizal.
- The Java Cryptography Architecture (JCA) and `javax.crypto` maintainers, whose primitives make a zero-dependency AES implementation practical.
- Open-source educators whose OOP and applied-crypto material informed the design of this project.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[contributors-url]: https://github.com/zcalifornia-ph/aes256-java/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[forks-url]: https://github.com/zcalifornia-ph/aes256-java/network/members
[stars-shield]: https://img.shields.io/github/stars/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[stars-url]: https://github.com/zcalifornia-ph/aes256-java/stargazers
[issues-shield]: https://img.shields.io/github/issues/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[issues-url]: https://github.com/zcalifornia-ph/aes256-java/issues
[license-shield]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge
[license-url]: https://github.com/zcalifornia-ph/aes256-java/blob/main/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/zcalifornia
[product-screenshot]: repo/images/project_screen.png
[Java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/
