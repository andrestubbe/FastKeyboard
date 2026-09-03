package fastkeyboard;

import java.util.List;

/**
 * Native Windows RawInput Keyboard API for Java.
 * Provides access to hardware-level keystrokes and multiple keyboard devices.
 */
public interface FastKeyboard extends AutoCloseable {

    /**
     * Creates a new global FastKeyboard listener instance.
     */
    static FastKeyboard open() {
        return new FastKeyboardImpl();
    }

    /**
     * Creates a new FastKeyboard instance bound to a specific Win32 window (HWND).
     * Keystrokes are only intercepted when the specified window has active focus.
     *
     * @param targetWindowHandle Native HWND of the target window.
     */
    static FastKeyboard openForWindow(long targetWindowHandle) {
        return new FastKeyboardImpl(targetWindowHandle);
    }

    // ═══════════════════════════════════════════════════════════
    // Events & Lifecycle
    // ═══════════════════════════════════════════════════════════

    /**
     * Starts listening for keyboard events in a dedicated native thread.
     * 
     * @param listener The callback for keyboard events.
     */
    void startListening(FastKeyboardListener listener);

    /**
     * Stops the native listening thread and releases resources.
     */
    void stopListening();

    @Override
    default void close() {
        stopListening();
    }

    // ═══════════════════════════════════════════════════════════
    // Normal Methods (Binding & Devices)
    // ═══════════════════════════════════════════════════════════

    /**
     * Binds input capture to a specific Win32 window handle.
     * When bound, events are only dispatched if the window is currently in the foreground.
     * Pass 0 to restore global capture.
     *
     * @param targetWindowHandle Native HWND
     */
    void bindToWindow(long targetWindowHandle);

    /**
     * Restores global input interception.
     */
    default void unbindFromWindow() {
        bindToWindow(0);
    }

    /**
     * Retrieves a list of all currently connected keyboard devices.
     * 
     * @return A list of KeyboardDevice objects.
     */
    List<KeyboardDevice> getConnectedDevices();

    // ═══════════════════════════════════════════════════════════
    // Is / Has
    // ═══════════════════════════════════════════════════════════

    /**
     * Checks if the native listener is currently active.
     */
    boolean isListening();

    /**
     * Returns whether keystroke capture is currently restricted to a specific window.
     */
    boolean isWindowBound();

    // ═══════════════════════════════════════════════════════════
    // Getter
    // ═══════════════════════════════════════════════════════════

    /**
     * Returns the currently bound window handle (HWND), or 0 if listening globally.
     */
    long getBoundWindow();
}
