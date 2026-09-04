package fastkeyboard;

import fastcore.FastCore;
import java.util.ArrayList;
import java.util.List;

/**
 * JNI Implementation of {@link FastKeyboard} using Win32 RawInput.
 *
 * <p>Dispatches raw hardware keyboard events directly from a dedicated native Windows message loop.</p>
 */
public class FastKeyboardImpl implements FastKeyboard {
    
    static {
        // Automatically extracts and loads the native DLL via FastCore
        FastCore.loadLibrary("fastkeyboard");
    }

    private long nativeHandle = 0;
    private long targetWindowHandle = 0;
    private boolean isListening = false;
    private FastKeyboardListener currentListener;

    /**
     * Creates a new global FastKeyboardImpl instance capturing keystrokes across the entire system.
     */
    public FastKeyboardImpl() {
        this(0);
    }

    /**
     * Creates a new FastKeyboardImpl instance bound to a specific Win32 window (HWND).
     *
     * @param targetWindowHandle Native HWND of the target window (or 0 for global capture)
     */
    public FastKeyboardImpl(long targetWindowHandle) {
        this.targetWindowHandle = targetWindowHandle;
    }

    /**
     * Starts listening for raw keyboard events on a background native thread.
     *
     * @param listener The callback interface for receiving keyboard input events
     */
    @Override
    public void startListening(FastKeyboardListener listener) {
        if (isListening) return;
        this.currentListener = listener;
        this.nativeHandle = nStart(targetWindowHandle);
        this.isListening = true;
    }

    /**
     * Stops listening for keyboard events and releases native resources.
     */
    @Override
    public void stopListening() {
        if (!isListening) return;
        nStop(nativeHandle);
        isListening = false;
        nativeHandle = 0;
    }

    /**
     * Binds keyboard capture to a specific Win32 window handle.
     *
     * @param targetWindowHandle Native HWND of the window to bind to (or 0 for global)
     */
    @Override
    public void bindToWindow(long targetWindowHandle) {
        this.targetWindowHandle = targetWindowHandle;
        if (nativeHandle != 0) {
            nBindWindow(nativeHandle, targetWindowHandle);
        }
    }

    /**
     * Enumerates and returns all currently connected raw input keyboard devices.
     *
     * @return List of {@link KeyboardDevice} descriptors
     */
    @Override
    public List<KeyboardDevice> getConnectedDevices() {
        List<KeyboardDevice> devices = new ArrayList<>();
        nGetDevices(devices);
        return devices;
    }

    /**
     * Checks whether the native keyboard listener thread is currently active.
     *
     * @return {@code true} if listening, {@code false} otherwise
     */
    @Override
    public boolean isListening() {
        return isListening;
    }

    /**
     * Checks whether keyboard input capture is currently bound to a specific window.
     *
     * @return {@code true} if bound to an HWND, {@code false} if global
     */
    @Override
    public boolean isWindowBound() {
        return targetWindowHandle != 0;
    }

    /**
     * Returns the native window handle (HWND) currently bound, or 0 if unbound.
     *
     * @return The native HWND handle
     */
    @Override
    public long getBoundWindow() {
        return targetWindowHandle;
    }

    /**
     * Called by C++ JNI layer when a key event occurs.
     *
     * @param deviceHandle The native handle of the physical keyboard
     * @param vKey The virtual key code (OS level)
     * @param makeCode The hardware scan code
     * @param isPressed {@code true} if key is down, {@code false} if up
     * @param isE0 {@code true} if extended key (e.g. right Alt)
     * @param timestamp System message timestamp in milliseconds
     * @param keyChar The translated character, or empty/null for non-text keys
     */
    private void dispatchKeyEvent(long deviceHandle, int vKey, int makeCode, boolean isPressed, boolean isE0, long timestamp, String keyChar) {
        if (currentListener != null) {
            currentListener.onKeyEvent(deviceHandle, vKey, makeCode, isPressed, isE0, timestamp, keyChar);
        }
    }

    /**
     * Resolves the current console window HWND handle.
     *
     * @return The HWND handle of the console window, or 0 if none
     */
    static long getConsoleWindowHandle() {
        return nGetConsoleWindow();
    }

    private native long nStart(long targetWindowHandle);
    private native void nBindWindow(long handle, long targetWindowHandle);
    private native void nStop(long handle);
    private native void nGetDevices(List<KeyboardDevice> outList);
    private static native long nGetConsoleWindow();
}
