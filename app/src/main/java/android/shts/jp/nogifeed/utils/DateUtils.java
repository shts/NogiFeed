package android.shts.jp.nogifeed.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy'年'MM'月'dd'日' HH:mm:ss");

    public static synchronized String formatUpdated(String source) {
        String updated = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = sdf.parse(source);
            updated = FORMATTER.format(date);

        } catch (ParseException e) {
            Log.e(TAG, "failed to parse");
            return null;
        }
        return updated;
    }
}
