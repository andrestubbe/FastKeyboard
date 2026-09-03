package fastkeyboard;

import java.util.List;

/**
 * FastKeyboard Demo — Demonstrates zero-latency hardware scancodes, device enumeration,
 * and window-focused vs. global capture modes.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   ⚡ FastKeyboard Native Demo (FastJava Standard) ⚡");
        System.out.println("=================================================");

        // 1. List connected devices
        System.out.println("\n[SYSTEM] Enumerating Keyboard Hardware Devices...");
        try (FastKeyboard keyboard = FastKeyboard.open()) {
            List<KeyboardDevice> devices = keyboard.getConnectedDevices();
            for (KeyboardDevice dev : devices) {
                System.out.println(" -> " + dev);
            }

            System.out.println("\n[ENGINE] Starting Keyboard Interception...");
            System.out.println(">>> Commands:");
            System.out.println("   [B] : Toggle Window Binding (Global vs. Console Window Focus)");
            System.out.println("   [ESC] : Exit Demo\n");

            // We can resolve current console window handle via GetConsoleWindow if available
            long consoleHwnd = 0;
            try {
                // Get console window handle via User32 Win32 call if available
                consoleHwnd = (long) Class.forName("com.sun.jna.platform.win32.Kernel32")
                        .getMethod("GetConsoleWindow").invoke(null);
            } catch (Throwable ignored) {
            }

            final long finalHwnd = consoleHwnd;

            keyboard.startListening((deviceHandle, vKey, makeCode, isPressed, isE0, timestamp, keyChar) -> {
                String state = isPressed ? "\033[92mDOWN\033[0m" : "\033[90mUP  \033[0m";
                String charDisplay = (keyChar != null && !keyChar.isEmpty()) ? "['\033[97m" + keyChar + "\033[0m']" : "    ";
                String modeBadge = keyboard.isWindowBound() 
                    ? "\033[96m[WINDOW-BOUND]\033[0m" 
                    : "\033[93m[GLOBAL]\033[0m";

                System.out.printf("%s \033[90m[\033[0m%s\033[90m]\033[0m \033[90mDev:\033[0m\033[97m%d\033[0m \033[90m|\033[0m \033[90mKey:\033[0m\033[97m%-12s\033[0m \033[90m(0x%02X)\033[0m \033[90m|\033[0m \033[90mScan:\033[0m\033[97m0x%02X\033[0m \033[90m|\033[0m %s\n",
                    modeBadge, state, deviceHandle, Keys.getName(vKey), vKey, makeCode, charDisplay);

                if (!isPressed) {
                    if (vKey == Keys.ESCAPE) {
                        System.out.println("\n\033[90m[ENGINE]\033[0m \033[97mESC detected. Exiting demo...\033[0m");
                        System.exit(0);
                    } else if (vKey == 0x42 && finalHwnd != 0) { // 'B' key
                        if (keyboard.isWindowBound()) {
                            keyboard.unbindFromWindow();
                            System.out.println("\n\033[93m>>> [MODE CHANGED] GLOBAL INTERCEPTION (All system-wide keys) <<<\033[0m\n");
                        } else {
                            keyboard.bindToWindow(finalHwnd);
                            System.out.println("\n\033[96m>>> [MODE CHANGED] WINDOW-BOUND FOCUS (Active Console Only) <<<\033[0m\n");
                        }
                    }
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
}
