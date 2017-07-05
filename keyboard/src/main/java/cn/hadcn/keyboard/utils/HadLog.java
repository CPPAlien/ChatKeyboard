package cn.hadcn.keyboard.utils;

import android.util.Log;

/**
 * @author chris
 */
public class HadLog {
    public static String LOG_TAG = "HadKeyboard";
	public static boolean enableLog = true;//true:enable log display

	private static final int RETURN_NOLOG = 99;

    @SuppressWarnings("unused")
	public static int d(String msg) {
        return enableLog ? Log.d(LOG_TAG + ":", msg) : RETURN_NOLOG;
    }

    @SuppressWarnings("unused")
	public static int e(String msg) {
        return enableLog ? Log.e(LOG_TAG + ":", msg) : RETURN_NOLOG;
    }

    @SuppressWarnings("unused")
    public static int e(String msg, Throwable e) {
        return enableLog ? Log.e(LOG_TAG + ":", msg, e) : RETURN_NOLOG;
    }

    @SuppressWarnings("unused")
    public static int i(String msg) {
        return enableLog ? Log.i(LOG_TAG + ":", msg) : RETURN_NOLOG;
    }

    @SuppressWarnings("unused")
    public static int w(String msg) {
        return enableLog ? Log.w(LOG_TAG + ":", msg) : RETURN_NOLOG;
    }
}
