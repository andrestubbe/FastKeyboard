# FastKeyboard v0.2.0 — Precision & Performance 🚀

## 🎉 Version 0.2.0: The Hardware-Level Update
**Release Date:** 2026-05-16  
**Tag:** `v0.2.0`

---

## ✨ Features

### ⏱️ High-Precision Timestamps
- Events now include a millisecond-accurate timestamp directly from the Windows message loop (`GetMessageTime`).
- Enables advanced typing rhythm analysis and behavioral telemetry.

### 🔠 Real-time Character Translation
- Integrated `ToUnicode` logic into the native JNI bridge.
- FastKeyboard now delivers the actual character (e.g., 'a', '@', 'S') instead of just raw scancodes.

### ⚡ Deterministic Performance
- Bypasses the standard Java AWT/Swing focus requirement.
- Background capture works system-wide with sub-1ms latency.

---

## 📦 Installation (JitPack)

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastKeyboard Module -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastKeyboard</artifactId>
        <version>0.2.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastKeyboard:0.2.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

---

## 🔧 Technical Details
- **Native DLL:** `fastkeyboard.dll` (v0.2.0, bundled inside `FastKeyboard-0.2.0.jar`).
- **Backend:** Win32 RawInput API (`WM_INPUT`).
- **Optimization:** Zero-allocation event dispatching loop.

---

## 🙏 Credits
- **FastCore:** Powered by the unified JNI loading engine.

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
