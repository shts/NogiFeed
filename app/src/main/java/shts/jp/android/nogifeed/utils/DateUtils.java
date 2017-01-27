package shts.jp.android.nogifeed.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import shts.jp.android.nogifeed.common.Logger;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    private DateUtils() {
    }

    private static final SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    private static final SimpleDateFormat to = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);

    /**
     * ブログ記事の日付( yyyy-MM-dd'T'HH:mm:ss.SSS )を yyyy-MM-dd HH:mm 形式に変換する
     * 変換に失敗した場合はパラメータをそのまま返す
     *
     * @param d 2016-11-27T11:57:00.000Z
     * @return yyyy-MM-dd HH:mm
     */
    public static String parse(String d) {
        try {
            return to.format(from.parse(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
