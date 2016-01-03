package shts.jp.android.nogifeed.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import shts.jp.android.nogifeed.common.Logger;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy'/'MM'/'dd HH:mm:ss");
    private static final SimpleDateFormat FORMATTER_FILE = new SimpleDateFormat("yyyyMMddHHmmss");

    // TODO: リンクの日付からファイル名を生成する
    public static synchronized String formatFileName(String source) {
        String updated = source;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = sdf.parse(source);
            updated = FORMATTER_FILE.format(date);

        } catch (ParseException e) {
            Logger.e(TAG, "failed to parse");
            // 2015/08/23 Sun
            updated = source.replace("/", "_").replace(" ", "_");
            updated = updated + "_";
            return updated;
        }
        return updated;
    }

    public static synchronized String formatUpdated(String source) {
        Logger.d(TAG, "formatUpdated source(" + source + ")");
        String updated = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = sdf.parse(source);
            updated = FORMATTER.format(date);

        } catch (ParseException e) {
            Logger.e(TAG, "failed to parse");
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
        Logger.i(TAG, "formatUpdatedLong : dateTime" + dateTime + ") dateLong(" + date.getTime() + ")");
        return date.getTime();
    }

    public static synchronized Date formatUpdatedDate(String source) throws ParseException {
        return FORMATTER.parse(formatUpdated(source));
    }

    public static String dateToString(Date date) {
        Calendar datetime = Calendar.getInstance();
        datetime.setTime(date);
        return datetime.get(Calendar.YEAR) + "/" + (datetime.get(Calendar.MONTH) + 1)
                + "/" + datetime.get(Calendar.DATE);
    }
}
