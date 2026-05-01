# FastKeyboard

## 1. Vision & Kernidee
**FastKeyboard** ist das native Java-Modul für direkten, rohen Tastatur-Input unter Windows, basierend auf der **Win32 Raw Input API (`WM_INPUT`)**.

Es löst die typischen Probleme von High-Level APIs (wie `WM_KEYDOWN` oder AWT `KeyListener`), indem es die Eingaben abfängt, *bevor* das Betriebssystem sie verarbeitet.

**Warum Raw Input für Tastaturen?**
- **Hardware Make-Codes statt Virtual Keys:** Du bekommst den physischen Scancode der Taste, noch bevor das Windows-Tastaturlayout (QWERTZ vs. QWERTY) oder IME (Input Method Editor) eingreift. 
- **Multi-Keyboard Support:** Ideal, um z.B. einen Nummernblock, Barcode-Scanner oder ein Makro-Pad (`StreamDeck`) als komplett eigenständiges Gerät zu behandeln. (Taste "A" auf Tastatur 1 macht etwas anderes als Taste "A" auf Tastatur 2).
- **Anti-Ghosting / N-Key Rollover:** Direkte Erfassung aller Hardware-Events in Echtzeit.

## 2. Java High-Level API

```java
public interface FastKeyboard {
    static FastKeyboard open() { return new FastKeyboardImpl(); }
    
    // Startet das Hardware-Tracking
    void startListening(FastKeyboardListener listener);
    
    // Liefert alle angeschlossenen Tastaturen/HID-Eingabegeräte
    List<KeyboardDevice> getConnectedDevices();
    
    void stopListening();
}

public interface FastKeyboardListener {
    // vKey = Virtual Key (OS Level)
    // makeCode = Hardware Scan Code
    void onKeyEvent(long deviceHandle, int vKey, int makeCode, boolean isPressed, boolean isE0);
}
```

## 3. C++ JNI Backend (Win32 Raw Input)
Ähnlich wie FastMouse nutzt FastKeyboard ein Message-Only-Window.

1. **Registrierung:** `RegisterRawInputDevices` mit `UsagePage = 0x01` (Generic Desktop) und `Usage = 0x06` (Keyboard). Flags: `RIDEV_INPUTSINK`, um auch Eingaben im Hintergrund zu erhalten.
2. **Event-Extraktion:** Bei `WM_INPUT` wird das struct `RAWKEYBOARD` gelesen.
3. **Erkennung spezieller Tasten:** Der `Flags`-Wert (z.B. `RI_KEY_E0`) wird ausgewertet, um zwischen linken und rechten Modifiern (z.B. L-CTRL vs R-CTRL) präzise zu unterscheiden.

## 4. Abgrenzung zu FastHotkey
- **FastHotkey** (`RegisterHotKey` / `SetWindowsHookEx`) ist dazu da, globale Tastenkürzel (z.B. `CTRL + ALT + F`) zu definieren, die auch System-Events blockieren können (Hooks).
- **FastKeyboard** (`RawInput`) blockiert keine Eingaben, sondern **liest den Hardware-Stream**. Es ist gedacht für Spiele, Instrumentierung, Makro-Software und detaillierte AI-Telemetrie.

## 5. Agent-Kit (KI-Integration)
Agenten können FastKeyboard nutzen, um Benutzereingaben zu monitoren (Telemetrie) oder dedizierte Makro-Tastaturen zu verwalten.

**JSON Schema Beispiel (Telemetrie Stream):**
```json
{
  "action": "start_keyboard_telemetry",
  "filters": {
    "device_handle": "specific_macro_pad"
  }
}
```
