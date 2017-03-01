package com.packtpub.apps.rxjava_essentials;

import android.util.Log;

import java.util.Locale;

/**
 * Created by hjsuc on 2017-03-01.
 */

public class L {
    public static final String TAG = "RXSTUDY";

    public static void d(String message) {
        Log.d(TAG, Thread.currentThread().getName()+":"+buildMessage(message, null));
    }


    private static String buildMessage(String format, Object... args) {
        String msg = args == null ? format : String.format(Locale.getDefault(), format, args);
        StackTraceElement[] trace = (new Throwable()).fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        int lineNumber = -1;
        String fileName = "<unknown>";

        for (int i = 2; i < trace.length; ++i) {
            Class clazz = trace[i].getClass();
            if (!clazz.equals(L.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf(46) + 1);//.
                callingClass = callingClass.substring(callingClass.lastIndexOf(36) + 1);//$
                caller = callingClass + " # " + trace[i].getMethodName();
                lineNumber = trace[i].getLineNumber();
                fileName = trace[i].getFileName();
                break;
            }
        }

        return String.format(Locale.getDefault(), "[%05d] %s : %s  (%s:%d)", new Object[]{Long.valueOf(Thread.currentThread().getId()), caller, msg, fileName, lineNumber});
    }

}
