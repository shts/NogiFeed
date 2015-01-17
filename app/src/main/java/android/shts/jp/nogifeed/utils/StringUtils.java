package android.shts.jp.nogifeed.utils;

import android.shts.jp.nogifeed.common.Logger;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static final int INDEX_FIRST_NAME = 0;
    public static final int INDEX_LAST_NAME = 1;

    /**
     * Get [member's a article url] from [member's full name].
     * [member's full name] -> String[nakada, kana].
     * [member's a article blog url] -> http://blog.nogizaka46.com/kana.nakada/2014/12/021877.php
     * @param articleUrl a article blog url -> http://blog.nogizaka46.com/kana.nakada/2014/12/021877.php
     * @return String array names. index 0 is first name. index 1 is last name.
     */
    public static String[] getFullNameFromArticleUrl(String articleUrl) {
        Logger.v(TAG, "getFullNameFromArticleUrl : articleUrl(" + articleUrl + ")");
        final String[] fullName = articleUrl.split("/");
        final String[] fullNameArray = fullName[3].split("\\.");
        for (int i = 0; i < fullNameArray.length; i++) {
            Logger.d(TAG, "index : " + i + " name : " + fullNameArray[i]);
        }
        return fullNameArray;
    }

    /**
     * Get [member full name] from [member all article blog url].
     * [member full name] -> String[akimoto, manatsu].
     * [member all article blog url] -> http://blog.nogizaka46.com/manatsu.akimoto/smph/
     * @param allArticleUrl member's all article url -> http://blog.nogizaka46.com/manatsu.akimoto/smph/
     * @return String array names. index 0 is first name. index 1 is last name.
     */
    public static String[] getFullNameFromAllArticleUrl(String allArticleUrl) {
        Logger.v(TAG, "getFullNameFromAllArticleUrl : allArticleUrl(" + allArticleUrl + ")");
        final String[] fullName = allArticleUrl.split("/");
        final String[] fullNameArray = fullName[3].split("\\.");
        for (int i = 0; i < fullNameArray.length; i++) {
            Logger.d(TAG, "index : " + i + " name : " + fullNameArray[i]);
        }
        return fullNameArray;
    }

    private static final String[] NEED_ADD_CHAR_LAST_NAME = {
        "ito", "saito", "noujo", "eto"
    };

    public static String addCharU(String lastName) {
        for (String needAddCharName : NEED_ADD_CHAR_LAST_NAME) {
            if (needAddCharName.equals(lastName)) {
                lastName += 'u';
            }
        }
        return lastName;
    }

    public static String ignoreCdataTagWithCrlf(String target) {
        // delete cdata tag
        String ignoreCdataStartTag = target.replace("<![CDATA[", "");
        String ignoreCdataEndTag = ignoreCdataStartTag.replace("]]>", "");
        String ignoreCrLf = ignoreCdataEndTag.replace("\n", "");
        return ignoreCrLf;
    }

}
