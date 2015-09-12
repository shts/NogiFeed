package shts.jp.android.nogifeed.utils;

import android.os.Handler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Member;
import shts.jp.android.nogifeed.providers.NogiFeedContent;

public class DataStoreUtils {

    private static final String TAG = DataStoreUtils.class.getSimpleName();
    private static final Handler HANDLER = new Handler();

    public static void saveWidgetProfileMember(
            final Context context, final Member member, final int appWidgetId) {
        Logger.v(TAG, "saveWidgetProfileMember : member(" + member.toString() + ") appWidgetId(" + appWidgetId + ")");
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                final ContentResolver cr = context.getContentResolver();
                ContentValues cv = member.toContentValues();
                cv.put(NogiFeedContent.ProfileWidget.KEY_WIDGET_ID, appWidgetId);
                cr.insert(NogiFeedContent.ProfileWidget.CONTENT_URI, cv);
            }
        });
    }

    public static int getUnReadCounter(final Context context, final String feedUrl) {
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.UnRead.KEY_FEED_URL + "=?";
        String[] selectionArgs = { feedUrl };

        Cursor c = cr.query(NogiFeedContent.UnRead.CONTENT_URI, NogiFeedContent.UnRead.sProjection,
                selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null. because of use favorite function first time");
            return 0;
        }
        final int count = c.getCount();
        Logger.d(TAG, "getUnReadCounter : feed(" + feedUrl + ") count(" + count + ")");
        c.close();
        return count;
    }

    public static void readArticle(final Context context, final String articleUrl) {
        Logger.v(TAG, "readArticle(Context, String) in : articleUrl(" + articleUrl + ")");

        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.UnRead.KEY_ARTICLE_URL + "=?";
        String[] selectionArgs = { articleUrl };

        Cursor c = cr.query(NogiFeedContent.UnRead.CONTENT_URI,
                NogiFeedContent.UnRead.sProjection,
                selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return;
        }
        if (c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndexOrThrow(
                    NogiFeedContent.UnRead.KEY_ID
            ));

            String deleteSelection = NogiFeedContent.UnRead.KEY_ID + "=?";
            String[] deleteSelectionArgs = { String.valueOf(id) };
            cr.delete(NogiFeedContent.UnRead.CONTENT_URI, deleteSelection, deleteSelectionArgs);
        } else {
            Logger.w(TAG, "cannot moveToFirst()");
        }
        c.close();
    }

    public static boolean hasAlreadyWidget(final Context context, final String feedUrl) {
        Logger.v(TAG, "hasAlreadyRead(String) in : feedUrl(" + feedUrl + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.ProfileWidget.KEY_FEED_URL + "=?";
        String[] selectionArgs = { feedUrl };

        Cursor c = cr.query(NogiFeedContent.ProfileWidget.CONTENT_URI,
                NogiFeedContent.ProfileWidget.sProjection,
                selection, selectionArgs, null);
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

    /**
     * Whether the article has already read. If has already read the article return true.
     * @param context
     * @param articleUrl
     * @return
     */
    public static boolean hasAlreadyRead(final Context context, final String articleUrl) {
        Logger.v(TAG, "hasAlreadyRead(String) in : article(" + articleUrl + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.UnRead.KEY_ARTICLE_URL + "=?";
        String[] selectionArgs = { articleUrl };

        Cursor c = cr.query(NogiFeedContent.UnRead.CONTENT_URI, NogiFeedContent.UnRead.sProjection,
                selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return true;
        }
        if (c.moveToFirst()) {
            c.close();
            return false;
        } else {
            Logger.w(TAG, "cannot moveToFirst()");
        }
        c.close();
        return true;
    }

    public static void allUnReadArticle(final Context context) {
        Logger.v(TAG, "allUnReadArticle(Context)");
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(NogiFeedContent.UnRead.CONTENT_URI, NogiFeedContent.UnRead.sProjection,
                null, null, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return;
        }
        if (c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndexOrThrow(NogiFeedContent.UnRead.KEY_ID));
            final String url = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.UnRead.KEY_ARTICLE_URL));
            final String feed = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.UnRead.KEY_FEED_URL));
            Logger.d(TAG, "id(" + id + ") url(" + url + ") feed(" + feed + ")");
        } else {
            Logger.w(TAG, "cannot move to first");
        }
        c.close();
    }

//    public static void allWidgetProfile(Context context) {
//        final ContentResolver cr = context.getContentResolver();
//        Cursor c = cr.query(NogiFeedContent.ProfileWidget.CONTENT_URI,
//                null, null, null, null);
//        if (c == null) {
//            Logger.w(TAG, "allWidgetProfile : cursor is null");
//            return;
//        }
//        if (c.moveToFirst()) {
//            do {
//                int id = c.getInt(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.KEY_WIDGET_ID));
//                Logger.i(TAG, "profile widget id(" + id + ")");
//            } while(c.moveToNext());
//        } else {
//            Logger.w(TAG, "allWidgetProfile : failed to moveToFirst()");
//        }
//        c.close();
//    }

}
