package io.enotes.sdk.utils;

import android.text.TextUtils;
import android.util.Log;


public class LogUtils {
    public static boolean LOG_FLAG = true;

    public static void i(String tag, String msg) {
        if (LOG_FLAG && !TextUtils.isEmpty(msg))
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (LOG_FLAG && !TextUtils.isEmpty(msg))
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (LOG_FLAG && !TextUtils.isEmpty(msg))
            Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LOG_FLAG && !TextUtils.isEmpty(msg))
            Log.d(tag, msg, tr);
    }
}
