package com.example.dbprovidertest.Utils;

import android.util.Log;

public class LogUtil {
    private static final String EXTRA_TAG = "DBProvider_";
    private static final int LOG_ERROR = 1 << 0;
    private static final int LOG_WARN = (1 << 1) + LOG_ERROR;
    private static final int LOG_INFO = (1 << 2) + LOG_WARN;
    private static final int LOG_DEBUG = (1 << 3) + LOG_INFO;
    private static final int LOG_LEVEL = LOG_DEBUG;

    public static final void d(String tag, String msg) {
        if ((LOG_LEVEL & LOG_DEBUG) > 0) {
            Log.d(EXTRA_TAG + tag, msg);
        }
    }

    public static final void i(String tag, String msg) {
        if ((LOG_LEVEL & LOG_INFO) > 0) {
            Log.i(EXTRA_TAG + tag, msg);
        }
    }

    public static final void w(String tag, String msg) {
        if ((LOG_LEVEL & LOG_WARN) > 0) {
            Log.w(EXTRA_TAG + tag, msg);
        }
    }

    public static final void e(String tag, String msg) {
        if ((LOG_LEVEL & LOG_ERROR) > 0) {
            Log.e(EXTRA_TAG + tag, msg);
        }
    }
}
