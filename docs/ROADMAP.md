# FastKeyboard Roadmap 🗺️

**Vision:** To provide the fastest possible native primitives for keyboard input by aggressively bypassing bottlenecks in standard Java.

## 🟢 v0.1.2: Initial Production Release (Current)
- [x] **Core Native Engine**: Win32 RawInput JNI implementation.
- [x] **Native Focus Gating**: Low-overhead HWND window focus gating.
- [x] **Blueprint Standards**: README, Reference, and Philosophy integration.
- [x] **Zero GC Overhead**: High-frequency sub-ms keystroke dispatching.

## 🟡 v0.2.0: Optimization Phase
- [ ] **SIMD Acceleration**: Implement AVX2/SSE4.2 paths for core loops.
- [ ] **Software Prefetching**: Optimize memory access patterns.
- [ ] **Alignment Enforcement**: Ensure zero-penalty memory boundaries.

## 🟠 v0.5.0: Platform & Logic Expansion
- [ ] **ARM NEON Port**: Parity for Apple Silicon/Mobile.
- [ ] **Advanced Features**: Multi-threaded paths and complex batch operations.

## 🔴 v1.0.0: Production Hardening
- [ ] **Full Stability Audit**: Long-run stress testing.
- [ ] **Enterprise Support**: NUMA-awareness and Large Pages support.

---
**Focus:** Performance is our USP. We optimize where Java stops.