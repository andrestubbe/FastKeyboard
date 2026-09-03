package fastkeyboard;

import fastcore.FastCore;
import java.util.ArrayList;
import java.util.List;

/**
 * JNI Implementation of FastKeyboard using Win32 RawInput.
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

    public FastKeyboardImpl() {
        this(0);
    }

    public FastKeyboardImpl(long targetWindowHandle) {
        this.targetWindowHandle = targetWindowHandle;
    }

    // ═══════════════════════════════════════════════════════════
    // Events & Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Override
    public void startListening(FastKeyboardListener listener) {
        if (isListening) return;
        this.currentListener = listener;
        this.nativeHandle = nStart(this, targetWindowHandle);
        this.isListening = true;
    }

    @Override
    public void stopListening() {
        if (!isListening) return;
        nStop(nativeHandle);
        isListening = false;
        nativeHandle = 0;
    }

    // ═══════════════════════════════════════════════════════════
    // Normal Methods (Binding & Devices)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void bindToWindow(long targetWindowHandle) {
        this.targetWindowHandle = targetWindowHandle;
        if (nativeHandle != 0) {
            nBindWindow(nativeHandle, targetWindowHandle);
        }
    }

    @Override
    public List<KeyboardDevice> getConnectedDevices() {
        List<KeyboardDevice> devices = new ArrayList<>();
        nGetDevices(devices);
        return devices;
    }

    // ═══════════════════════════════════════════════════════════
    // Is / Has
    // ═══════════════════════════════════════════════════════════

    @Override
    public boolean isListening() {
        return isListening;
    }

    @Override
    public boolean isWindowBound() {
        return targetWindowHandle != 0;
    }

    // ═══════════════════════════════════════════════════════════
    // Getter
    // ═══════════════════════════════════════════════════════════

    @Override
    public long getBoundWindow() {
        return targetWindowHandle;
    }

    // ═══════════════════════════════════════════════════════════
    // Native Callbacks & Methods
    // ═══════════════════════════════════════════════════════════

    /**
     * Called by C++ JNI layer when a key event occurs.
     */
    private void dispatchKeyEvent(long deviceHandle, int vKey, int makeCode, boolean isPressed, boolean isE0, long timestamp, String keyChar) {
        if (currentListener != null) {
            currentListener.onKeyEvent(deviceHandle, vKey, makeCode, isPressed, isE0, timestamp, keyChar);
        }
    }

    private native long nStart(Object callbackObject, long targetWindowHandle);
    private native void nBindWindow(long handle, long targetWindowHandle);
    private native void nStop(long handle);
    private native void nGetDevices(List<KeyboardDevice> outList);
}
