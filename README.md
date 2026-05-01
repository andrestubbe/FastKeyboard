# FastKeyboard — Native Windows RawInput API for Java

**Low-latency, background-capable raw keyboard interception for Java.**

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

FastKeyboard provides **system-wide keyboard access** by tapping directly into the Windows `RawInput` API. It bypasses the standard OS message queue translation, giving you raw hardware scancodes and the ability to distinguish between different physical keyboards.

## Key Features

- **🚀 Native Performance** — Direct Win32 RawInput access via JNI.
- **⚡ Background Capture** — Intercept keys even when your Java app is minimized or hidden.
- **🛡️ Hardware Scancodes** — Get immutable "Make Codes" instead of layout-dependent virtual keys.
- **📦 Multi-Device Support** — Identify which physical keyboard sent the input (ideal for macro-pads).
- **📦 Zero GC Overhead** — High-performance event dispatching with minimal memory pressure.

## Quick Start

```java
FastKeyboard keyboard = new FastKeyboardImpl();

keyboard.startListening((handle, vKey, scanCode, pressed, isExtended) -> {
    System.out.printf("Key %s: ScanCode 0x%X on Device %d\n", 
        pressed ? "DOWN" : "UP", scanCode, handle);
});
```

## Installation

Requires **FastCore** for native library loading.

```xml
<dependency>
    <groupId>io.github.andrestubbe</groupId>
    <artifactId>fastkeyboard</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Building from Source

1. Run `compile.bat` (requires Visual Studio Build Tools).
2. Run `mvn clean package`.

---
**Made with ⚡ by Andre Stubbe**
