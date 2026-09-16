# Building FastKeyboard from Source

## Prerequisites

- **JDK 17+** — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **Visual Studio 2019/2022** — Community, Professional, or BuildTools with C++ Desktop development

## Quick Build

```bash
# 1. Build native DLL first (Windows)
compile.bat

# 2. Build JAR & install locally
mvn clean install -DskipTests
```

## Build Commands

| Command | Purpose |
|---|---|
| `compile.bat` | Compiles native C++ DLL (`fastkeyboard.dll`) via MSVC `cl.exe`. |
| `mvn clean compile` | Compiles Java sources only. |
| `mvn clean package` | Builds release JAR with native DLL embedded in resources. |
| `mvn test` | Executes unit tests. |

## Native DLL Build

The `compile.bat` script:
- Auto-detects Visual Studio installation via `vswhere.exe`.
- Auto-detects `JAVA_HOME`.
- Compiles `native\fastkeyboard.cpp` with optimization (`/O2 /LD /EHsc`).
- Links against `user32.lib` and `gdi32.lib`.
- Copies the resulting `fastkeyboard.dll` into:
  - `release\`
  - `src\main\resources\native\` (packaged directly into the final JAR)
  - `%USERPROFILE%\.fastcore\native\fastkeyboard\` (for FastCore unified loader cache)

## Troubleshooting

**"Cannot find DLL" / UnsatisfiedLinkError**:
1. Run `compile.bat` to compile `release\fastkeyboard.dll`.
2. Ensure the DLL is in `src\main\resources\native\` before packaging with Maven.
3. Verify that `JAVA_HOME` points to a 64-bit JDK 17+.
