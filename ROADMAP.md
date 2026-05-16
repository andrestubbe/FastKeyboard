# FastKeyboard Roadmap 🗺️

This roadmap outlines the evolution of the **FastKeyboard** module within the **FastJava** ecosystem.

## v0.1.0 — The Foundation (Released)
- [x] Win32 RawInput implementation (`WM_INPUT`).
- [x] Global background interception (no focus required).
- [x] Multi-device support (HID handle tracking).
- [x] Basic JNI bridge and `FastCore` loading.
- [x] Maven structure and JitPack readiness.

## v0.2.0 — Telemetry & Analysis (Current)
- [x] **High-Precision Timestamps**: Millisecond-accurate event timing for rhythm analysis.
- [x] **Character Translation**: `ToUnicode` integration for real-time text reconstruction.
- [ ] **Performance Benchmarking**: Comparative "Race" test against standard AWT/Swing.

## v1.0.0 — GhostType Integration (Upcoming)
- [ ] **Event Buffering**: Stateful buffer for word and phrase reconstruction.
- [ ] **State Tracking**: Handling of modifier keys (Shift, AltGr) across multiple devices.
- [ ] **Semantic Awareness**: Backspace and correction detection logic.
- [ ] **AI-Ready Streams**: Optimized JSON/Binary output for agentic language modeling.

## Future Vision
- **Cross-Platform Bridge**: Potential abstractions for Linux (`evdev`) and macOS (`Quartz`).
- **Deep Integration**: Direct binding for `FastUIA` to link typing behavior with specific UI components.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*
