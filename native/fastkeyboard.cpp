#include <jni.h>
#include <windows.h>
#include <string>
#include <vector>
#include <thread>
#include <atomic>

// JNI Reference caching
static jmethodID g_dispatchMethod = nullptr;
static jobject g_javaObject = nullptr;
static JavaVM* g_jvm = nullptr;

struct KeyboardThreadContext {
    HWND hwnd = NULL;
    HWND targetHwnd = NULL; // Focus-gating window handle (NULL = global capture)
    std::atomic<bool> running{ false };
    std::thread loopThread;
};

static KeyboardThreadContext* g_ctx = nullptr;

// Helper: Convert Wide string to UTF-8
std::string WideToUTF8(const std::wstring& wstr) {
    if (wstr.empty()) return std::string();
    int size_needed = WideCharToMultiByte(CP_UTF8, 0, &wstr[0], (int)wstr.size(), NULL, 0, NULL, NULL);
    std::string strTo(size_needed, 0);
    WideCharToMultiByte(CP_UTF8, 0, &wstr[0], (int)wstr.size(), &strTo[0], size_needed, NULL, NULL);
    return strTo;
}

// Check if target window or any of its children/hosts is currently focused
static inline bool IsWindowFocused(HWND targetHwnd) {
    if (targetHwnd == NULL) return true;
    HWND fgWindow = GetForegroundWindow();
    if (fgWindow == NULL) return false;
    if (fgWindow == targetHwnd) return true;

    // Direct ancestor & parent checks
    if (GetAncestor(targetHwnd, GA_ROOT) == fgWindow) return true;
    if (GetAncestor(targetHwnd, GA_ROOTOWNER) == fgWindow) return true;
    if (GetAncestor(fgWindow, GA_ROOT) == targetHwnd) return true;
    if (GetAncestor(fgWindow, GA_ROOT) == GetAncestor(targetHwnd, GA_ROOT)) return true;

    // Walk parent hierarchy to handle embedded hosts
    HWND parent = targetHwnd;
    while ((parent = GetParent(parent)) != NULL) {
        if (parent == fgWindow) return true;
    }
    parent = fgWindow;
    while ((parent = GetParent(parent)) != NULL) {
        if (parent == targetHwnd) return true;
    }

    return false;
}

// Window Procedure to handle Raw Input
LRESULT CALLBACK RawInputWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    if (msg == WM_INPUT) {
        // Fast Window-Focus Gating: If target window is specified, only process when active!
        if (g_ctx && g_ctx->targetHwnd != NULL && !IsWindowFocused(g_ctx->targetHwnd)) {
            return 0; // Target window does NOT have focus -> Zero CPU overhead, skip!
        }

        UINT dwSize;
        GetRawInputData((HRAWINPUT)lParam, RID_INPUT, NULL, &dwSize, sizeof(RAWINPUTHEADER));
        
        std::vector<BYTE> lpb(dwSize);
        if (GetRawInputData((HRAWINPUT)lParam, RID_INPUT, lpb.data(), &dwSize, sizeof(RAWINPUTHEADER)) != dwSize) {
            return 0;
        }

        RAWINPUT* raw = (RAWINPUT*)lpb.data();
        if (raw->header.dwType == RIM_TYPEKEYBOARD) {
            const RAWKEYBOARD& rkb = raw->data.keyboard;
            
            bool isPressed = (rkb.Flags & RI_KEY_BREAK) == 0;
            bool isE0 = (rkb.Flags & RI_KEY_E0) != 0;
            long timestamp = GetMessageTime();

            // --- Character Translation ---
            std::wstring keyCharStr;
            if (isPressed) {
                BYTE keyboardState[256];
                GetKeyboardState(keyboardState);

                wchar_t buffer[16] = {0};
                int result = ToUnicode(rkb.VKey, rkb.MakeCode, keyboardState, buffer, 16, 0);
                if (result > 0) {
                    keyCharStr = std::wstring(buffer, result);
                }
            }
            
            JNIEnv* env;
            if (g_jvm->AttachCurrentThread((void**)&env, NULL) == JNI_OK) {
                jstring jKeyChar = keyCharStr.empty() ? NULL : env->NewString((jchar*)keyCharStr.c_str(), (jsize)keyCharStr.length());
                
                env->CallVoidMethod(g_javaObject, g_dispatchMethod, 
                    (jlong)raw->header.hDevice, 
                    (jint)rkb.VKey, 
                    (jint)rkb.MakeCode, 
                    (jboolean)isPressed, 
                    (jboolean)isE0,
                    (jlong)timestamp,
                    jKeyChar);

                if (jKeyChar) env->DeleteLocalRef(jKeyChar);
            }
        }
    }
    return DefWindowProc(hwnd, msg, wParam, lParam);
}

// Thread function for the message loop
void MessageLoop(KeyboardThreadContext* ctx) {
    WNDCLASSEX wc = {0};
    wc.cbSize = sizeof(WNDCLASSEX);
    wc.lpfnWndProc = RawInputWndProc;
    wc.hInstance = GetModuleHandle(NULL);
    wc.lpszClassName = "FastKeyboardInternal";
    RegisterClassEx(&wc);

    // Note: RIDEV_INPUTSINK requires a top-level window (cannot be HWND_MESSAGE)!
    ctx->hwnd = CreateWindowEx(
        WS_EX_TOOLWINDOW, wc.lpszClassName, "FastKeyboardHiddenWindow",
        WS_POPUP, 0, 0, 0, 0, NULL, NULL, wc.hInstance, NULL
    );

    RAWINPUTDEVICE rid;
    rid.usUsagePage = 0x01;
    rid.usUsage = 0x06;
    rid.dwFlags = RIDEV_INPUTSINK; // Global capture!
    rid.hwndTarget = ctx->hwnd;
    RegisterRawInputDevices(&rid, 1, sizeof(rid));

    MSG msg;
    while (ctx->running && GetMessage(&msg, NULL, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
}

extern "C" {

JNIEXPORT jlong JNICALL Java_fastkeyboard_FastKeyboardImpl_nStart(JNIEnv* env, jobject obj, jlong targetWindowHandle) {
    env->GetJavaVM(&g_jvm);
    g_javaObject = env->NewGlobalRef(obj);
    jclass clazz = env->GetObjectClass(obj);
    g_dispatchMethod = env->GetMethodID(clazz, "dispatchKeyEvent", "(JIIZZJLjava/lang/String;)V");

    KeyboardThreadContext* ctx = new KeyboardThreadContext();
    ctx->targetHwnd = (HWND)targetWindowHandle;
    ctx->running = true;
    g_ctx = ctx;
    ctx->loopThread = std::thread(MessageLoop, ctx);
    
    return (jlong)ctx;
}

JNIEXPORT void JNICALL Java_fastkeyboard_FastKeyboardImpl_nBindWindow(JNIEnv* env, jobject obj, jlong handle, jlong targetWindowHandle) {
    KeyboardThreadContext* ctx = (KeyboardThreadContext*)handle;
    if (ctx) {
        ctx->targetHwnd = (HWND)targetWindowHandle;
    }
}

JNIEXPORT void JNICALL Java_fastkeyboard_FastKeyboardImpl_nStop(JNIEnv* env, jobject obj, jlong handle) {
    KeyboardThreadContext* ctx = (KeyboardThreadContext*)handle;
    if (ctx) {
        ctx->running = false;
        PostMessage(ctx->hwnd, WM_CLOSE, 0, 0);
        if (ctx->loopThread.joinable()) ctx->loopThread.join();
        
        env->DeleteGlobalRef(g_javaObject);
        if (g_ctx == ctx) g_ctx = nullptr;
        delete ctx;
    }
}

JNIEXPORT void JNICALL Java_fastkeyboard_FastKeyboardImpl_nGetDevices(JNIEnv* env, jobject obj, jobject jList) {
    UINT nDevices;
    GetRawInputDeviceList(NULL, &nDevices, sizeof(RAWINPUTDEVICELIST));
    
    std::vector<RAWINPUTDEVICELIST> deviceList(nDevices);
    GetRawInputDeviceList(deviceList.data(), &nDevices, sizeof(RAWINPUTDEVICELIST));

    jclass listClass = env->GetObjectClass(jList);
    jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");
    jclass deviceClass = env->FindClass("fastkeyboard/KeyboardDevice");
    jmethodID deviceCtor = env->GetMethodID(deviceClass, "<init>", "(JLjava/lang/String;Ljava/lang/String;)V");

    for (const auto& dev : deviceList) {
        if (dev.dwType == RIM_TYPEKEYBOARD) {
            // Get Device Name/Path
            UINT nameSize;
            GetRawInputDeviceInfoA(dev.hDevice, RIDI_DEVICENAME, NULL, &nameSize);
            std::string name(nameSize, 0);
            GetRawInputDeviceInfoA(dev.hDevice, RIDI_DEVICENAME, &name[0], &nameSize);

            jstring jName = env->NewStringUTF("Keyboard");
            jstring jPath = env->NewStringUTF(name.c_str());
            
            jobject deviceObj = env->NewObject(deviceClass, deviceCtor, (jlong)dev.hDevice, jName, jPath);
            env->CallBooleanMethod(jList, addMethod, deviceObj);
            
            env->DeleteLocalRef(jName);
            env->DeleteLocalRef(jPath);
            env->DeleteLocalRef(deviceObj);
        }
    }
}

static BOOL CALLBACK FindTerminalChildEnum(HWND hwnd, LPARAM lParam) {
    char className[256];
    if (GetClassNameA(hwnd, className, sizeof(className))) {
        if (strstr(className, "TermControl") != NULL || 
            strstr(className, "Console") != NULL ||
            strstr(className, "VirtualConsole") != NULL) {
            if (IsWindowVisible(hwnd)) {
                *(HWND*)lParam = hwnd;
                return FALSE; // Found active visible terminal panel, stop!
            }
        }
    }
    return TRUE;
}

JNIEXPORT jlong JNICALL Java_fastkeyboard_FastKeyboardImpl_nGetConsoleWindow(JNIEnv* env, jclass clazz) {
    HWND hwnd = GetConsoleWindow();
    
    // Check if we are hosted under Windows Terminal or another root container
    HWND hwndForeground = GetForegroundWindow();
    if (hwndForeground != NULL) {
        bool isOurWindow = (hwndForeground == hwnd);
        if (!isOurWindow) {
            HWND parent = hwnd;
            while (parent != NULL) {
                if (parent == hwndForeground) {
                    isOurWindow = true;
                    break;
                }
                parent = GetParent(parent);
            }
            if (!isOurWindow) {
                if (GetAncestor(hwnd, GA_ROOT) == hwndForeground || GetAncestor(hwnd, GA_ROOTOWNER) == hwndForeground) {
                    isOurWindow = true;
                }
            }
        }
        if (isOurWindow) {
            HWND hwndTerminalChild = NULL;
            EnumChildWindows(hwndForeground, FindTerminalChildEnum, (LPARAM)&hwndTerminalChild);
            if (hwndTerminalChild != NULL) {
                hwnd = hwndTerminalChild;
            } else {
                hwnd = hwndForeground;
            }
        }
    }
    
    return (jlong)hwnd;
}

} // extern "C"
