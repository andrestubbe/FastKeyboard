package fastkeyboard;

/**
 * Represents a physical keyboard device connected to the system.
 */
public record KeyboardDevice(long handle, String name, String devicePath) {
    @Override
    public String toString() {
        return String.format("KeyboardDevice[Handle=%d, Name=%s]", handle, name);
    }
}
