package shts.jp.android.nogifeed.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Member;
import shts.jp.android.nogifeed.providers.NogiFeedContent;

import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.CONTENT_URI;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.sProjection;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_ID;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_WIDGET_ID;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_NAME;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_IMAGE_URL;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_ARTICLE_URL;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_FEED_URL;

public class ProfileWidget {

    private static final String TAG = ProfileWidget.class.getSimpleName();

    public static void save(
            final Context context, final Member member, final int appWidgetId) {
        Logger.v(TAG, "profile widget appWidgetId(" + appWidgetId + ")");
        final ContentResolver cr = context.getContentResolver();
        final ContentValues cv = member.toContentValues();
        cv.put(KEY_WIDGET_ID, appWidgetId);
        cr.insert(CONTENT_URI, cv);
    }

    public static boolean exist(final Context context, final String feedUrl) {
        Logger.v(TAG, "exist(String) in : feedUrl(" + feedUrl + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_FEED_URL + "=?";
        String[] selectionArgs = { feedUrl };

        Cursor c = cr.query(CONTENT_URI, sProjection, selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return false;
        }
        if (!c.moveToFirst()) {
            Logger.w(TAG, "cannot moveToFirst()");
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }

    public static void dump(Context context) {
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, null, null, null, null);
        if (c == null) {
            Logger.w(TAG, "allWidgetProfile : cursor is null");
            return;
        }
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(KEY_WIDGET_ID));
                Logger.i(TAG, "profile widget id(" + id + ")");
            } while(c.moveToNext());
        } else {
            Logger.w(TAG, "allWidgetProfile : failed to moveToFirst()");
        }
        c.close();
    }
}
