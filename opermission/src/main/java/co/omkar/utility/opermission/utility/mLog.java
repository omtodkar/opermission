package co.omkar.utility.opermission.utility;

import android.util.Log;

/**
 * <p>Controller logger.</p>
 * Created on 19/08/16.
 *
 * @author Omkar Todkar.
 */
public final class mLog {

    private static boolean mode;

    private static boolean isMode() {
        return mode;
    }

    public static void setDebugMode(boolean mode) {
        mLog.mode = mode;
    }

    public static int v(String tag, String msg) {
        return isMode() ? Log.v(tag, msg) : -1;
    }

    public static int v(String tag, String msg, Throwable tr) {
        return isMode() ? Log.v(tag, msg, tr) : -1;
    }

    public static int d(String tag, String msg) {
        return isMode() ? Log.d(tag, msg) : -1;
    }

    public static int d(String tag, String msg, Throwable tr) {
        return isMode() ? Log.d(tag, msg, tr) : -1;
    }

    public static int i(String tag, String msg) {
        return isMode() ? Log.i(tag, msg) : -1;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return isMode() ? Log.i(tag, msg, tr) : -1;
    }

    public static int w(String tag, String msg) {
        return isMode() ? Log.w(tag, msg) : -1;
    }

    public static int w(String tag, String msg, Throwable tr) {
        return isMode() ? Log.w(tag, msg, tr) : -1;
    }

    public static int w(String tag, Throwable tr) {
        return isMode() ? Log.w(tag, tr) : -1;
    }

    public static int e(String tag, String msg) {
        return isMode() ? Log.e(tag, msg) : -1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return isMode() ? Log.e(tag, msg, tr) : -1;
    }

    public static int wtf(String tag, String msg) {
        return isMode() ? Log.e(tag, msg) : -1;
    }

    public static int wtf(String tag, Throwable tr) {
        return isMode() ? Log.wtf(tag, tr) : -1;
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        return isMode() ? Log.wtf(tag, msg, tr) : -1;
    }

    public static String getStackTraceString(Throwable tr) {
        return isMode() ? Log.getStackTraceString(tr) : null;
    }

    public static int println(int priority, String tag, String msg) {
        return isMode() ? Log.println(priority, tag, msg) : -1;
    }
}
