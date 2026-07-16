package fastkeyboard;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Windows Virtual-Key Codes for FastKeyboard.
 * Provides static constants for key codes and methods to look up key names.
 */
public final class Keys {
    private Keys() {}

    // Mouse buttons
    public static final int LBUTTON = 0x01;
    public static final int RBUTTON = 0x02;
    public static final int CANCEL = 0x03;
    public static final int MBUTTON = 0x04;
    public static final int XBUTTON1 = 0x05;
    public static final int XBUTTON2 = 0x06;

    // Control keys
    public static final int BACK = 0x08;
    public static final int BACKSPACE = 0x08; // Alias
    public static final int TAB = 0x09;
    public static final int CLEAR = 0x0C;
    public static final int RETURN = 0x0D;
    public static final int ENTER = 0x0D; // Alias
    public static final int SHIFT = 0x10;
    public static final int CONTROL = 0x11;
    public static final int CTRL = 0x11; // Alias
    public static final int MENU = 0x12;
    public static final int ALT = 0x12; // Alias
    public static final int PAUSE = 0x13;
    public static final int CAPITAL = 0x14;
    public static final int CAPS_LOCK = 0x14; // Alias
    public static final int KANA = 0x15;
    public static final int HANGUL = 0x15;
    public static final int JUNJA = 0x17;
    public static final int FINAL = 0x18;
    public static final int HANJA = 0x19;
    public static final int KANJI = 0x19;
    public static final int ESCAPE = 0x1B;
    public static final int ESC = 0x1B; // Alias
    public static final int CONVERT = 0x1C;
    public static final int NONCONVERT = 0x1D;
    public static final int ACCEPT = 0x1E;
    public static final int MODECHANGE = 0x1F;
    public static final int SPACE = 0x20;
    public static final int PRIOR = 0x21;
    public static final int PAGE_UP = 0x21; // Alias
    public static final int NEXT = 0x22;
    public static final int PAGE_DOWN = 0x22; // Alias
    public static final int END = 0x23;
    public static final int HOME = 0x24;
    public static final int LEFT = 0x25;
    public static final int UP = 0x26;
    public static final int RIGHT = 0x27;
    public static final int DOWN = 0x28;
    public static final int SELECT = 0x29;
    public static final int PRINT = 0x2A;
    public static final int EXECUTE = 0x2B;
    public static final int SNAPSHOT = 0x2C;
    public static final int PRINTSCREEN = 0x2C; // Alias
    public static final int INSERT = 0x2D;
    public static final int DELETE = 0x2E;
    public static final int HELP = 0x2F;

    // Number keys 0-9 (ASCII values)
    public static final int KEY_0 = 0x30;
    public static final int KEY_1 = 0x31;
    public static final int KEY_2 = 0x32;
    public static final int KEY_3 = 0x33;
    public static final int KEY_4 = 0x34;
    public static final int KEY_5 = 0x35;
    public static final int KEY_6 = 0x36;
    public static final int KEY_7 = 0x37;
    public static final int KEY_8 = 0x38;
    public static final int KEY_9 = 0x39;

    // Letters A-Z (ASCII values)
    public static final int A = 0x41;
    public static final int B = 0x42;
    public static final int C = 0x43;
    public static final int D = 0x44;
    public static final int E = 0x45;
    public static final int F = 0x46;
    public static final int G = 0x47;
    public static final int H = 0x48;
    public static final int I = 0x49;
    public static final int J = 0x4A;
    public static final int K = 0x4B;
    public static final int L = 0x4C;
    public static final int M = 0x4D;
    public static final int N = 0x4E;
    public static final int O = 0x4F;
    public static final int P = 0x50;
    public static final int Q = 0x51;
    public static final int R = 0x52;
    public static final int S = 0x53;
    public static final int T = 0x54;
    public static final int U = 0x55;
    public static final int V = 0x56;
    public static final int W = 0x57;
    public static final int X = 0x58;
    public static final int Y = 0x59;
    public static final int Z = 0x5A;

    // Windows keys
    public static final int LWIN = 0x5B;
    public static final int RWIN = 0x5C;
    public static final int APPS = 0x5D;
    public static final int SLEEP = 0x5F;

    // Numpad
    public static final int NUMPAD0 = 0x60;
    public static final int NUMPAD1 = 0x61;
    public static final int NUMPAD2 = 0x62;
    public static final int NUMPAD3 = 0x63;
    public static final int NUMPAD4 = 0x64;
    public static final int NUMPAD5 = 0x65;
    public static final int NUMPAD6 = 0x66;
    public static final int NUMPAD7 = 0x67;
    public static final int NUMPAD8 = 0x68;
    public static final int NUMPAD9 = 0x69;
    public static final int MULTIPLY = 0x6A;
    public static final int ADD = 0x6B;
    public static final int SEPARATOR = 0x6C;
    public static final int SUBTRACT = 0x6D;
    public static final int DECIMAL = 0x6E;
    public static final int DIVIDE = 0x6F;

    // Function keys
    public static final int F1 = 0x70;
    public static final int F2 = 0x71;
    public static final int F3 = 0x72;
    public static final int F4 = 0x73;
    public static final int F5 = 0x74;
    public static final int F6 = 0x75;
    public static final int F7 = 0x76;
    public static final int F8 = 0x77;
    public static final int F9 = 0x78;
    public static final int F10 = 0x79;
    public static final int F11 = 0x7A;
    public static final int F12 = 0x7B;
    public static final int F13 = 0x7C;
    public static final int F14 = 0x7D;
    public static final int F15 = 0x7E;
    public static final int F16 = 0x7F;
    public static final int F17 = 0x80;
    public static final int F18 = 0x81;
    public static final int F19 = 0x82;
    public static final int F20 = 0x83;
    public static final int F21 = 0x84;
    public static final int F22 = 0x85;
    public static final int F23 = 0x86;
    public static final int F24 = 0x87;

    // Num Lock, Scroll Lock
    public static final int NUMLOCK = 0x90;
    public static final int SCROLL = 0x91;

    // Left/Right modifier keys
    public static final int LSHIFT = 0xA0;
    public static final int RSHIFT = 0xA1;
    public static final int LCONTROL = 0xA2;
    public static final int RCONTROL = 0xA3;
    public static final int LMENU = 0xA4; // Left Alt
    public static final int LALT = 0xA4; // Alias
    public static final int RMENU = 0xA5; // Right Alt
    public static final int RALT = 0xA5; // Alias

    // Browser Keys
    public static final int BROWSER_BACK = 0xA6;
    public static final int BROWSER_FORWARD = 0xA7;
    public static final int BROWSER_REFRESH = 0xA8;
    public static final int BROWSER_STOP = 0xA9;
    public static final int BROWSER_SEARCH = 0xAA;
    public static final int BROWSER_FAVORITES = 0xAB;
    public static final int BROWSER_HOME = 0xAC;

    // Volume Keys
    public static final int VOLUME_MUTE = 0xAD;
    public static final int VOLUME_DOWN = 0xAE;
    public static final int VOLUME_UP = 0xAF;

    // Media Keys
    public static final int MEDIA_NEXT_TRACK = 0xB0;
    public static final int MEDIA_PREV_TRACK = 0xB1;
    public static final int MEDIA_STOP_ATTENTION = 0xB2;
    public static final int MEDIA_PLAY_PAUSE = 0xB3;

    // Launch Keys
    public static final int LAUNCH_MAIL = 0xB4;
    public static final int LAUNCH_MEDIA_SELECT = 0xB5;
    public static final int LAUNCH_APP1 = 0xB6;
    public static final int LAUNCH_APP2 = 0xB7;

    // OEM Keys
    public static final int OEM_1 = 0xBA; // ';:' key
    public static final int OEM_PLUS = 0xBB; // '+' key
    public static final int OEM_COMMA = 0xBC; // ',' key
    public static final int OEM_MINUS = 0xBD; // '-' key
    public static final int OEM_PERIOD = 0xBE; // '.' key
    public static final int OEM_2 = 0xBF; // '/?' key
    public static final int OEM_3 = 0xC0; // '`~' key
    public static final int OEM_4 = 0xDB; // '[{' key
    public static final int OEM_5 = 0xDC; // '\|' key
    public static final int OEM_6 = 0xDD; // ']}' key
    public static final int OEM_7 = 0xDE; // '"'' key
    public static final int OEM_8 = 0xDF;
    public static final int OEM_102 = 0xE2; // '<>' or '\|' key

    public static final int PROCESSKEY = 0xE5;
    public static final int PACKET = 0xE7;
    public static final int ATTN = 0xF6;
    public static final int CRSEL = 0xF7;
    public static final int EXSEL = 0xF8;
    public static final int EREOF = 0xF9;
    public static final int PLAY = 0xFA;
    public static final int ZOOM = 0xFB;
    public static final int NONAME = 0xFC;
    public static final int PA1 = 0xFD;
    public static final int OEM_CLEAR = 0xFE;

    // Mapping maps
    private static final Map<Integer, String> codeToName = new HashMap<>();
    private static final Map<String, Integer> nameToCode = new HashMap<>();

    static {
        for (Field field : Keys.class.getFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                try {
                    String name = field.getName();
                    int value = field.getInt(null);
                    // Avoid overwriting primary names with aliases
                    if (!codeToName.containsKey(value)) {
                        codeToName.put(value, name);
                    }
                    nameToCode.put(name.toUpperCase(), value);
                } catch (IllegalAccessException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Gets the standard name of a Virtual-Key code.
     * 
     * @param vKey The Virtual-Key code.
     * @return The key name, or "UNKNOWN_0xXX" if not recognized.
     */
    public static String getName(int vKey) {
        String name = codeToName.get(vKey);
        if (name != null) {
            return name;
        }
        return String.format("UNKNOWN_0x%02X", vKey);
    }

    /**
     * Gets the Virtual-Key code for a given key name (case-insensitive).
     * 
     * @param name The name of the key (e.g. "ESCAPE", "ENTER").
     * @return The key code, or null if not found.
     */
    public static Integer getCode(String name) {
        if (name == null) return null;
        return nameToCode.get(name.toUpperCase());
    }

    /**
     * Gets a read-only copy of the code-to-name mapping.
     */
    public static Map<Integer, String> getCodeToNameMap() {
        return Map.copyOf(codeToName);
    }

    /**
     * Gets a read-only copy of the name-to-code mapping.
     */
    public static Map<String, Integer> getNameToCodeMap() {
        return Map.copyOf(nameToCode);
    }
}
