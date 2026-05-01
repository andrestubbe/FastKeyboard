package fastkeyboard;

/**
 * Real-time listener for hardware keyboard events via RawInput.
 */
public interface FastKeyboardListener {
    /**
     * Called when a keyboard event is captured.
     * 
     * @param deviceHandle The native handle of the keyboard device.
     * @param vKey The Windows Virtual Key code.
     * @param makeCode The hardware scancode (Make Code).
     * @param isPressed True if the key was pressed, false if released.
     * @param isE0 True if the E0 extended bit is set (e.g., for right-side modifiers).
     */
    void onKeyEvent(long deviceHandle, int vKey, int makeCode, boolean isPressed, boolean isE0);
}
