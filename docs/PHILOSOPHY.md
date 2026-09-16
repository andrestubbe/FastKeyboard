# The Philosophy of FastKeyboard

> [!IMPORTANT]
> **"Zero Copies. Never. Critical JNI Path. Native-First Performance."**

FastKeyboard is built on the principle that modern Java applications require **native-first** acceleration for performance-critical operations that the standard JVM APIs don't fully optimize.

## Core Tenets

1.  **Native-First Execution**
    Bypass standard Java AWT/Swing input layers to reach the physical limits of the hardware using direct Win32 RawInput (`WM_INPUT`) message handling.

2.  **Zero-Copy JNI Architecture**
    Minimize JNI transition costs by caching method IDs and passing raw primitive arguments directly between the Win32 message loop and Java listeners without intermediate heap object allocations.

3.  **Deterministic Latency**
    Eliminate input variance caused by JIT warm-up, AWT Event Dispatch Thread stalls, or garbage collection pauses in latency-critical gaming and telemetry loops.

4.  **Hardware-Aware Optimization**
    Capture uncompressed physical hardware make codes directly from the keyboard controller before Windows OS keyboard layout translation or key repeat throttling.

5.  **Blueprint Consistency**
    As part of the **FastJava** ecosystem, FastKeyboard adheres to a standardized architecture:
    *   **Native Backend**: Direct Win32 C++ implementation with native HWND focus gating.
    *   **Unified Loading**: Powered by `FastCore`.
    *   **Premium Quality**: Built for high-performance systems and autonomous agents.

---
**⚡ FastKeyboard — Powering the next generation of Native Java.**
