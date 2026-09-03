# Changelog

All notable changes to this project will be documented in this file.

## [0.1.1] - 2026-08-19

### Added
- **Native Window-Focus Gating**: Added `openForWindow(hwnd)`, `bindToWindow(hwnd)`, and `unbindFromWindow()`. Keystrokes are filtered natively in C++ via `GetForegroundWindow()`, eliminating background typing leaks and cutting inactive CPU overhead to zero.
- **FastJava Standard Method Ordering**: Enforced strict ordering: Events -> Normal Methods -> Is/Has -> Getter -> Setter -> Native.
- **AutoCloseable**: FastKeyboard now implements `AutoCloseable` with automatic cleanup in try-with-resources.
- **FastANSI Demo**: Interactive terminal demo styled with FastANSI gray/bright-white theme and runtime window-binding toggle (`[B]`).
- **Modern FastJava Documentation**: Aligned `README.md`, `CHANGELOG.md`, and `REFERENCE.md` with the latest FastVulkan / FastAnimation blueprint standard.

## [0.1.0] - 2026-05-23

### Added
- Initial release
- Win32 RawInput keyboard interception via JNI
- Immutable hardware make codes and multi-keyboard device enumeration
