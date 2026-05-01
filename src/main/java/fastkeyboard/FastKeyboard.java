package fastkeyboard;

import java.util.List;

/**
 * Native Windows RawInput Keyboard API for Java.
 * Provides access to hardware-level keystrokes and multiple keyboard devices.
 */
public interface FastKeyboard {
    
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

    /**
     * Retrieves a list of all currently connected keyboard devices.
     * 
     * @return A list of KeyboardDevice objects.
     */
    List<KeyboardDevice> getConnectedDevices();

    /**
     * Checks if the native listener is currently active.
     */
    boolean isListening();
}
