package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtils {

    private PreferencesUtils() {}

    private static SharedPreferences getDefaultSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(Context context, String key, String defValue) {
        return getDefaultSharedPreferences(context).getString(key, defValue);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        return getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

}
