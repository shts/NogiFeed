package android.shts.jp.nogifeed.utils;

import android.util.Log;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static final int INDEX_FIRST_NAME = 0;
    public static final int INDEX_LAST_NAME = 1;

    public static String[] createFullNameFrom(String memberFeedUrl) {
        final String[] fullName = memberFeedUrl.split("/");
        final String[] fullNameArray = fullName[3].split("\\.");
        for (int i = 0; i < fullNameArray.length; i++) {
            Log.i(TAG, "index : " + i + " name : " + fullNameArray[i]);
        }
        return fullNameArray;
    }

    private static final String[] NEED_ADD_CHAR_LAST_NAME = {
        "ito", "saito", "nojo", "eto"
    };

    public static String addCharU(String lastName) {
        for (String needAddCharName : NEED_ADD_CHAR_LAST_NAME) {
            if (needAddCharName.equals(lastName)) {
                lastName += 'u';
            }
        }
        return lastName;
    }

}
