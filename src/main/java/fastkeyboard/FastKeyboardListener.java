package fastkeyboard;

/**
 * Real-time listener for hardware keyboard events via RawInput.
 */
public interface FastKeyboardListener {
    /**
     * Called when a keyboard event occurs.
     * 
     * @param deviceHandle The native handle of the physical keyboard.
     * @param vKey The virtual key code (OS level).
     * @param makeCode The hardware scan code.
     * @param isPressed True if key is down, false if up.
     * @param isE0 True if extended key (e.g. right Alt).
     * @param timestamp System message timestamp in milliseconds.
     * @param keyChar The translated character (e.g. 'a', '@'). Can be null/empty for non-text keys.
     */
    void onKeyEvent(long deviceHandle, int vKey, int makeCode, boolean isPressed, boolean isE0, long timestamp, String keyChar);
}
