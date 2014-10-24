package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.shts.jp.nogifeed.common.IPreferences;

public class PreferenceUtils implements IPreferences {

    private static SharedPreferences getDefaultPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isFirstBoot(Context context) {
        return getDefaultPref(context).getBoolean(Key.FIRST_BOOT, false);
    }

    public static void setFirstBoot(Context context, boolean isFirstBoot) {
        getDefaultPref(context).edit().putBoolean(Key.FIRST_BOOT, isFirstBoot).commit();
    }

    public static boolean useFavorite(Context context) {
        return getDefaultPref(context).getBoolean(Key.FAVORITE, false);
    }

    public static void useFavorite(Context context, boolean use) {
        getDefaultPref(context).edit().putBoolean(Key.FAVORITE, use).commit();
    }

    public static boolean useNotification(Context context) {
        return getDefaultPref(context).getBoolean(Key.NOTIFICATION, false);
    }

    public static void useNotification(Context context, boolean use) {
        getDefaultPref(context).edit().putBoolean(Key.NOTIFICATION, use).commit();
    }

}
