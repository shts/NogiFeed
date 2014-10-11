package android.shts.jp.nogifeed.utils;

import android.util.Log;

public class LogUtils {

    private static final boolean DEBUG = false;

    public static void log(String message) {
        if (DEBUG) return;
    }

    public static void log(String tag, String message) {
        if (DEBUG) Log.v(tag, message);
    }
}
