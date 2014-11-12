package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.shts.jp.nogifeed.common.IPreferences;

public class PreferenceUtils implements IPreferences {

    private static SharedPreferences getDefaultPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        getDefaultPref(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defVal) {
        return getDefaultPref(context).getBoolean(key, defVal);
    }
}
