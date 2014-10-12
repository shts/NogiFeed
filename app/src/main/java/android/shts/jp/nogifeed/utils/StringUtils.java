package android.shts.jp.nogifeed.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static final int INDEX_FIRST_NAME = 0;
    public static final int INDEX_LAST_NAME = 1;

//    private static final String MATCHER_PATTERN = "<img.+?src=\\s*(?:[\\\"'])?([^ \\\"']*)[^>]*>";
    private static final String MATCHER_PATTERN = "<img src=\\s*(?:[\\\"'])?([^ \\\"']*)[^>]*>";

    public static String[] createFullNameFrom(String memberFeedUrl) {
        final String[] fullName = memberFeedUrl.split("/");
        final String[] fullNameArray = fullName[3].split("\\.");
        for (int i = 0; i < fullNameArray.length; i++) {
            Log.i(TAG, "index : " + i + " name : " + fullNameArray[i]);
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

    public static List<String> getThumnailImage(String content, int maxSize) {

        if (TextUtils.isEmpty(content)) {
            Log.e(TAG, "failed to getThumnailImage() : content is null");
            return null;
        }

        List<String> imageUrls = new ArrayList<String>();

        String contentWithoutCDataTag = ignoreCDataTag(content);
        String contentWithoutCrLf = ignoreLinefeed(contentWithoutCDataTag);

        Pattern pattern = Pattern.compile(MATCHER_PATTERN, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(contentWithoutCrLf);

        while (matcher.find()) {
            String matchText = matcher.group();
            if (!isGifFile(matchText)) {
                imageUrls.add(ignoreImgTag(matchText));
            }
            if (maxSize < imageUrls.size()) break;
        }
        return imageUrls;
    }

    private static boolean isGifFile(String matchText) {
        return matchText.contains(".gif");
    }

    public static String ignoreImgTag(String content) {
        Log.v(TAG, "ignoreImgTag : content (" + content + ")");

        // ignore img tags
        String ignored = content.replace("<img src=", "");
        ignored = ignored.replace("/>", "");
        ignored = ignored.replace(">", "");

        // ignore double quotation
        ignored = ignored.replace("\"", "");

        // ignore style elements
        ignored = ignored.replace("style=max-width:100%;", "");

        Log.v(TAG, "ignoreImgTag : ignored (" + ignored + ")");
        return ignored;
    }

    public static String ignoreCDataTag(String content) {
        String deleteCDataStartTag = content.replace("![CDATA[", "");
        String deleteCDataEndTag = deleteCDataStartTag.replace("]]>", "");
        return deleteCDataEndTag;
    }

    public static String ignoreLinefeed(String content) {
        return content.replace("\n", "");
    }

}
