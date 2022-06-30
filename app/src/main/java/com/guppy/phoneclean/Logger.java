package com.guppy.phoneclean;

import android.util.Log;

public final class Logger {
    private static final String TAG = Logger.class.getName();

    public static final void log(Object o) {
        if (!IConstant.IS_DEBUG) return;
        if (o != null) {
            String str = o.toString();
            Log.v(TAG, str);
        }
    }
}
