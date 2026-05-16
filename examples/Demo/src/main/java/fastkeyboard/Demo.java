package fastkeyboard;

import java.util.List;

/**
 * FastKeyboard Demo — Demonstrates background interception and raw hardware scancodes.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   ⚡ FastKeyboard Native Demo (v0.2.0) ⚡");
        System.out.println("===========================================");

        FastKeyboard keyboard = new FastKeyboardImpl();

        // 1. List connected devices
        System.out.println("\n[SYSTEM] Enumerating Keyboard Devices...");
        List<KeyboardDevice> devices = keyboard.getConnectedDevices();
        for (KeyboardDevice dev : devices) {
            System.out.println(" -> " + dev);
        }

        // 2. Start global listening
        System.out.println("\n[ENGINE] Starting Background Interception...");
        System.out.println(">>> TRY PRESSING KEYS WHILE THIS WINDOW IS MINIMIZED! <<<");
        System.out.println(">>> PRESS 'ESC' TO STOP THE DEMO <<<\n");

        keyboard.startListening((deviceHandle, vKey, makeCode, isPressed, isE0, timestamp, keyChar) -> {
            String state = isPressed ? "DOWN" : "UP  ";
            String charDisplay = (keyChar != null && !keyChar.isEmpty()) ? "['" + keyChar + "']" : "    ";
            
            System.out.printf("[%d] [%s] Handle:%d | VKey:0x%02X | ScanCode:0x%02X | %s\n", 
                timestamp, state, deviceHandle, vKey, makeCode, charDisplay);

            // Exit on Escape key (VKey 0x1B)
            if (vKey == 0x1B && !isPressed) {
                System.out.println("\n[ENGINE] ESC detected. Stopping...");
                keyboard.stopListening();
                System.exit(0);
            }
        });

        // Keep the main thread alive while the native thread works
        try {
            while (keyboard.isListening()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
