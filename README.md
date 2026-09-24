[Index](https://github.com/andrestubbe) · [Previous](https://github.com/andrestubbe/FastHotkey) | [Next](https://github.com/andrestubbe/FastKeylogger)

# FastKeyboard 0.1.2 [2026-09-04] — Ultra-Fast Native RawInput Keyboard Engine for Java

[![Status](https://img.shields.io/badge/status-0.1.2-brightgreen.svg)](https://github.com/andrestubbe/FastKeyboard/releases/tag/0.1.2)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastKeyboard)

---

**⚡ High-performance, zero-latency Win32 RawInput keyboard interception and window-focus gating for Java.**

**FastKeyboard** provides hardware-level keystroke interception, immutable physical make codes, multi-keyboard hardware identification, and native window-focus gating (`HWND`). Built for game engines, high-speed terminal emulators, low-latency UI frameworks, and telemetry tools with zero JVM Garbage Collection overhead.

[**Watch Showcase Demo (YouTube)**](https://youtu.be/Jpq8W5mcRIM)

[![FastKeyboard Showcase](docs/screenshot.png)](https://youtu.be/Jpq8W5mcRIM)

---

## Quick Start

```java
import fastkeyboard.FastKeyboard;
import fastkeyboard.Keys;

public class Demo {
    public static void main(String[] args) {
        // Global interception or window-bound capture
        try (FastKeyboard keyboard = FastKeyboard.open()) {

            // Optional: Bind to specific window (HWND) to capture ONLY when active
            // keyboard.bindToWindow(window.getHWND());

            keyboard.startListening((deviceHandle, vKey, makeCode, isPressed, isE0, timestamp, keyChar) -> {
                System.out.printf("[%s] Key: %s (0x%02X) | ScanCode: 0x%02X | Char: '%s'\n",
                    isPressed ? "DOWN" : "UP  ", Keys.getName(vKey), vKey, makeCode, keyChar);
            });

            // Keep main thread alive
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## Table of Contents

- [Quick Start](#quick-start)
- [Why FastKeyboard?](#why-fastkeyboard)
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Window Binding & Focus Gating](#window-binding--focus-gating)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [Related Projects](#related-projects)
- [License](#license)

---

## Why FastKeyboard?

Standard Java keyboard handling (like AWT `KeyListener`, JavaFX, or polling `GetAsyncKeyState`) fails under real-world performance requirements:

- **Layout Dependency**: AWT reports translated virtual keys that break on foreign keyboard layouts (QWERTZ vs. QWERTY vs. AZERTY).
- **Missing Hardware IDs**: Traditional APIs cannot distinguish between a main mechanical keyboard, a barcode scanner, or an external macro keypad.
- **Polling Lag & Ghosting**: Polling mechanisms waste CPU cycles and drop sub-millisecond keystrokes.
- **Focus Leaks**: Global hooks often intercept keys indiscriminately when typing in other applications.

**FastKeyboard** solves this fundamentally:

- **True Hardware Scancodes**: Intercepts the physical "Make Code" directly from the HID controller before OS layout translation.
- **Multi-Device Disambiguation**: Tracks the raw `hDevice` handle per keystroke.
- **Zero CPU Overhead**: Purely event-driven native message loop with JNI method caching.
- **Native Window Focus Gating**: Natively filters keystrokes by `HWND` in C++ — zero context switches or Java events when the window is in the background.

| Feature | Java AWT KeyListener | JNativeHook | FastKeyboard |
|:---|:---|:---|:---|
| **Input Pipeline** | OS translated virtual keys | Global `WH_KEYBOARD_LL` hook | **Win32 RawInput (`WM_INPUT`)** |
| **Physical Scancodes** | Layout-dependent char mapping | Virtual key codes | **Immutable hardware make codes** |
| **Multi-Device Support**| Single aggregated keyboard stream| Aggregated stream only | **Individual `hDevice` hardware IDs** |
| **Focus Gating** | Active window only | Intercepts all OS keys | **Native Win32 `HWND` focus filter** |

---

## Key Features

- ⚡ **Direct Win32 RawInput (`WM_INPUT`)** — Bypasses standard OS latency buffers.
- 🎯 **Native Window-Focus Gating** — Bind capture to a specific window (`bindToWindow(hwnd)`).
- 🔑 **Immutable Hardware Make Codes** — Perfect for game controls, keybinds, and rhythm games.
- 🖥️ **Multi-Keyboard Disambiguation** — Distinguishes between multiple connected keyboards/scanners.
- 🌑 **Background Capture** — Intercepts keys even when your Java app is minimized or hidden (when unbound).
- 📦 **Zero GC Pressure** — High-performance event dispatching with zero heap allocations in the hot path.
- 🧹 **Clean FastJava Lifecycle** — Implements `AutoCloseable` with complete native resource cleanup.

---

## Real-World Use Cases

- 🎮 **Competitive Gaming & Rhythm Engines**: Intercept unbuffered raw keystrokes at sub-millisecond precision without the GC stutter or event coalescing of the AWT Event Dispatch Thread (EDT).
- 📟 **High-Speed Terminal Emulators ([FastTerminal](https://github.com/andrestubbe/FastTerminal)) & TUI ([FastTUI](https://github.com/andrestubbe/FastTUI))**: Ultra-responsive CLI interfaces with native focus gating, ensuring keystrokes only register when the console window is active.
- 🏷️ **Industrial Barcode & RFID Scanners**: Multi-device HID tracking separates incoming automated scanner input streams from manual user typing on the same workstation.
- ⌨️ **Global Hotkey & Desktop Telemetry**: Background hotkey engines that reliably capture key chords across multi-monitor environments even when the app is minimized.

---

## Performance Benchmarks

FastKeyboard is profiled using **JMH (Java Microbenchmark Harness)** to guarantee zero overhead.

| Benchmark / Operation | Score (ops/ms) | Ops per Second |
|---|---|---|
| **`benchmarkGetConnectedDevices`** | **~70,621 ops/ms** | **> 70.6 Million** |
| **Raw Keystroke Dispatch Throughput** | **~2,800 ops/ms** | **> 2.8 Million** |

*Measured on Windows 11, Intel Core i5-1135G7 (Surface Pro 8), JDK 21.0.12.1. Native message loop bypasses standard OS queues to deliver sub-microsecond response times (< 350 ns) with zero GC pressure.*

---

## API Quick Reference

| Method | Return Type | Description | Docs |
|---|---|---|---|
| `FastKeyboard.open()` | `FastKeyboard` | Factory method to create a new global capture listener. | [Reference](docs/REFERENCE.md#factory-methods) |
| `FastKeyboard.openForWindow(hwnd)` | `FastKeyboard` | Factory method bound to a specific Win32 window handle (`HWND`). | [Reference](docs/REFERENCE.md#factory-methods) |
| `FastKeyboard.getConsoleWindow()` | `long` | Retrieves native console window handle (`HWND`) if running in a console. | [Reference](docs/REFERENCE.md#factory-methods) |
| `startListening(listener)` | `void` | Begins background raw input capture in a dedicated native thread. | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `stopListening()` | `void` | Stops the background listener thread and cleans up native resources. | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `bindToWindow(hwnd)` | `void` | Focus-gates capture to the specified window handle (`0` = global). | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `unbindFromWindow()` | `void` | Restores global capture across all desktop windows. | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `getConnectedDevices()` | `List<KeyboardDevice>` | Lists all attached HID keyboard hardware devices. | [Reference](docs/REFERENCE.md#device-querying) |
| `isListening()` | `boolean` | Returns `true` if the native message loop is currently active. | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `isWindowBound()` | `boolean` | Returns `true` if capture is bound to a specific window. | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `getBoundWindow()` | `long` | Returns the bound `HWND` (or `0` if global). | [Reference](docs/REFERENCE.md#event-capture--window-binding) |
| `close()` | `void` | Releases native hooks and frees unmanaged resources. | [Reference](docs/REFERENCE.md#event-capture--window-binding) |

---

## Window Binding & Focus Gating

FastKeyboard supports seamless switching between **Global Interception** (for bots, hotkeys, screen recorders) and **Window-Bound Capture** (for UI, games, FastVulkan, FastTerminal):

```java
FastKeyboard keyboard = FastKeyboard.open();

// 1. Capture system-wide (Global Hook mode)
keyboard.unbindFromWindow();

// 2. Capture ONLY when target window has foreground focus (UI mode)
keyboard.bindToWindow(window.getHWND());
```

> [!NOTE]
> Window focus verification happens directly in the native C++ message loop via `GetForegroundWindow()`. When the window is inactive, events are discarded immediately with **0 JNI traversals** and **0 JVM allocations**.

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Interactive Terminal Demo** | [Demo.java](examples/Demo/src/main/java/fastkeyboard/Demo.java) | `run-demo.bat` | Live keystroke monitor styled with FastANSI gray & bright-white theme. Supports toggling between Global and Window-Bound focus via `[B]`. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastkeyboard/benchmark/Benchmark.java) | `run-benchmark.bat` | JMH benchmark suite measuring raw event dispatch throughput and hardware enumeration speed. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastKeyboard Library -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastKeyboard</artifactId>
        <version>0.1.2</version>
    </dependency>
    <!-- Required Native JNI loader -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastKeyboard:0.1.2'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 📦 **[FastKeyboard-0.1.2.jar](https://github.com/andrestubbe/FastKeyboard/releases/download/0.1.2/FastKeyboard-0.1.2.jar)** (The Core Library with embedded native DLL)
2. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the JNI calls to function correctly.

---

## Documentation

- **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
- **[REFERENCE.md](docs/REFERENCE.md)**: Exhaustive catalog of API descriptions, focus gating, and scancodes.
- **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Zero-allocation and low-overhead processing designs.
- **[ROADMAP.md](docs/ROADMAP.md)**: Planned milestone features and performance extensions.
- **[CHANGELOG.md](docs/CHANGELOG.md)**: Complete version history and release notes.

---

## Platform Support

| Platform | Architecture | Status | Driver / Subsystem |
|:---|:---:|:---:|:---|
| **Windows 10 / 11** | x64 | ✅ Fully Supported | Native Win32 `WM_INPUT` (RawInput) |
| **Linux** | x64 / AArch64 | 🚧 Planned | `evdev` / `libinput` Direct Keyboard Stream |
| **macOS** | Apple Silicon / x64 | 🚧 Planned | Quartz Event Taps (`CGEventTap`) |

---

## Related Projects

- **[`FastCore`](https://github.com/andrestubbe/FastCore)** — Native library loader and ecosystem runtime substrate for Java
- **[`FastHotkey`](https://github.com/andrestubbe/FastHotkey)** — Low-latency global hotkey engine for Java
- **[`FastKeylogger`](https://github.com/andrestubbe/FastKeylogger)** — Low-latency keyboard event capture and logging
- **[`FastMouse`](https://github.com/andrestubbe/FastMouse)** — Ultra-low latency native raw input mouse engine for Java
- **[`FastMouseLogger`](https://github.com/andrestubbe/FastMouseLogger)** — High-frequency mouse telemetry and event logging
- **[`FastTouch`](https://github.com/andrestubbe/FastTouch)** — Multi-touch and pointer input capture for Java
- **[`FastStylus`](https://github.com/andrestubbe/FastStylus)** — Pen and stylus input tracking for Java
- **[`FastGamepad`](https://github.com/andrestubbe/FastGamepad)** — Gamepad input handling for Java

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.* 🚀
