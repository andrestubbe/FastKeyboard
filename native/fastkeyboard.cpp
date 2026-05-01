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
    HWND hwnd;
    std::atomic<bool> running;
    std::thread loopThread;
};

// Helper: Convert Wide string to UTF-8
std::string WideToUTF8(const std::wstring& wstr) {
    if (wstr.empty()) return std::string();
    int size_needed = WideCharToMultiByte(CP_UTF8, 0, &wstr[0], (int)wstr.size(), NULL, 0, NULL, NULL);
    std::string strTo(size_needed, 0);
    WideCharToMultiByte(CP_UTF8, 0, &wstr[0], (int)wstr.size(), &strTo[0], size_needed, NULL, NULL);
    return strTo;
}

// Window Procedure to handle Raw Input
LRESULT CALLBACK RawInputWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    if (msg == WM_INPUT) {
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
            
            JNIEnv* env;
            if (g_jvm->AttachCurrentThread((void**)&env, NULL) == JNI_OK) {
                env->CallVoidMethod(g_javaObject, g_dispatchMethod, 
                    (jlong)raw->header.hDevice, 
                    (jint)rkb.VKey, 
                    (jint)rkb.MakeCode, 
                    (jboolean)isPressed, 
                    (jboolean)isE0);
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

    ctx->hwnd = CreateWindowEx(0, wc.lpszClassName, NULL, 0, 0, 0, 0, 0, HWND_MESSAGE, NULL, wc.hInstance, NULL);

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

JNIEXPORT jlong JNICALL Java_fastkeyboard_FastKeyboardImpl_nStart(JNIEnv* env, jobject obj) {
    env->GetJavaVM(&g_jvm);
    g_javaObject = env->NewGlobalRef(obj);
    jclass clazz = env->GetObjectClass(obj);
    g_dispatchMethod = env->GetMethodID(clazz, "dispatchKeyEvent", "(JIIZZ)V");

    KeyboardThreadContext* ctx = new KeyboardThreadContext();
    ctx->running = true;
    ctx->loopThread = std::thread(MessageLoop, ctx);
    
    return (jlong)ctx;
}

JNIEXPORT void JNICALL Java_fastkeyboard_FastKeyboardImpl_nStop(JNIEnv* env, jobject obj, jlong handle) {
    KeyboardThreadContext* ctx = (KeyboardThreadContext*)handle;
    if (ctx) {
        ctx->running = false;
        PostMessage(ctx->hwnd, WM_CLOSE, 0, 0);
        if (ctx->loopThread.joinable()) ctx->loopThread.join();
        
        env->DeleteGlobalRef(g_javaObject);
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

} // extern "C"
