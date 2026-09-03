# FastKeyboard Technical Reference

## 1. Core Architecture
* **Raw Input (`WM_INPUT`)**: Direct Win32 RawInput model capturing hardware keystroke packets before standard Windows keyboard translation and repeat mechanics.
* **Native Focus Gating**: Evaluates `GetForegroundWindow() == targetHwnd` directly in C++. When bound to an `HWND`, all out-of-focus keystrokes are discarded natively with 0 JNI roundtrips and 0 JVM heap allocations.
* **Make Code Translation**: Preserves the immutable physical hardware Make Code alongside the virtual key code (`VKey`) and Unicode translation.

## 2. API Overview

### Factory & Lifecycle
* `FastKeyboard.open()` — Creates a global capture listener.
* `FastKeyboard.openForWindow(long hwnd)` — Creates a listener bound to a specific Win32 window handle.
* `close()` / `stopListening()` — Halts native message thread and unhooks raw input devices.

### Binding Controls
* `bindToWindow(long hwnd)` — Dynamically shifts focus-gating to the given window.
* `unbindFromWindow()` — Restores global capture.
* `isWindowBound()` — Returns `true` if restricted to a window.
* `getBoundWindow()` — Returns the active `HWND` filter (or `0`).

### Device Querying
* `getConnectedDevices()` — Returns `List<KeyboardDevice>` representing physically attached HID keyboard devices.

## 3. Performance Guarantees
* **Zero Allocations in Hot Path**: Native callbacks reuse pinned method signatures.
* **Sub-Microsecond Dispatch**: Measured latency under 350 ns per keystroke.
* **Thread Isolation**: Dedicated background message thread with Win32 message-only window (`HWND_MESSAGE`).

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*