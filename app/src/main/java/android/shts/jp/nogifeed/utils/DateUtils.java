package android.shts.jp.nogifeed.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy'/'MM'/'dd HH:mm:ss");

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

    public static synchronized long formatUpdatedLong(String source) throws ParseException {
        Date date = FORMATTER.parse(formatUpdated(source));
        String dateTime = "year(" + date.getYear() + ") month(" + date.getMonth()
                + ") day(" + date.getDay() + ") hour(" + date.getHours()
                + ") minute(" + date.getMinutes() + ") seconds(" + date.getSeconds()
                + ") ";
        Log.i(TAG, "formatUpdatedLong : dateTime" + dateTime + ") dateLong(" + date.getTime() + ")");
        return date.getTime();
    }

    public static synchronized Date formatUpdatedDate(String source) throws ParseException {
        return FORMATTER.parse(formatUpdated(source));
    }
}
