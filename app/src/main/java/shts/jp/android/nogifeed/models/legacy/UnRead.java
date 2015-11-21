package shts.jp.android.nogifeed.models.legacy;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.providers.NogiFeedContent;
import shts.jp.android.nogifeed.utils.UrlUtils;

import static shts.jp.android.nogifeed.providers.NogiFeedContent.UnRead.CONTENT_URI;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.UnRead.sProjection;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.UnRead.KEY_FEED_URL;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.UnRead.KEY_ID;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.UnRead.KEY_ARTICLE_URL;

// TODO: 未読専用のリストを作成する
// 現実装ではrss reader より古い情報もWidgetに表示されてしまうため
public class UnRead {

    private static final String TAG = UnRead.class.getSimpleName();

    public static int count(Context context, String feedUrl) {
        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_FEED_URL + "=?";
        String[] selectionArgs = { feedUrl };

        Cursor c = cr.query(CONTENT_URI, sProjection, selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null. because of use favorite function first time");
            return 0;
        }
        final int count = c.getCount();
        Logger.d(TAG, "getUnReadCounter : feed(" + feedUrl + ") count(" + count + ")");
        c.close();
        return count;
    }

    public static boolean exist(final Context context, final String articleUrl) {
        Logger.v(TAG, "hasAlreadyRead(String) in : article(" + articleUrl + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_ARTICLE_URL + "=?";
        String[] selectionArgs = { articleUrl };

        Cursor c = cr.query(CONTENT_URI, sProjection, selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return false;
        }
        if (c.moveToFirst()) {
            c.close();
            return true;
        } else {
            Logger.w(TAG, "cannot moveToFirst()");
        }
        c.close();
        return false;
    }

    public static void newUnReadArticle(final Context context, final String articleUrl) {
        if (exist(context, articleUrl)) {
            return;
        }
        final String feedUrl = UrlUtils.getMemberFeedUrl(articleUrl);
        final ContentValues cv = new ContentValues();
        cv.put(KEY_FEED_URL, feedUrl);
        cv.put(KEY_ARTICLE_URL, articleUrl);
        context.getContentResolver().insert(CONTENT_URI, cv);
    }

    public static void readComplete(final Context context, final String articleUrl) {
        Logger.v(TAG, "read(Context, String) in : articleUrl(" + articleUrl + ")");

        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_ARTICLE_URL + "=?";
        String[] selectionArgs = { articleUrl };

        Cursor c = cr.query(CONTENT_URI, sProjection,
                selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return;
        }
        if (c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));

            String deleteSelection = KEY_ID + "=?";
            String[] deleteSelectionArgs = { String.valueOf(id) };
            cr.delete(CONTENT_URI, deleteSelection, deleteSelectionArgs);
        } else {
            Logger.w(TAG, "cannot moveToFirst()");
        }
        c.close();
    }

    public static void dump(final Context context) {
        Logger.v(TAG, "allUnReadArticle(Context)");
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, sProjection,
                null, null, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return;
        }
        if (c.moveToFirst()) {
            do {
                final int id = c.getInt(c.getColumnIndexOrThrow(NogiFeedContent.UnRead.KEY_ID));
                final String url = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.UnRead.KEY_ARTICLE_URL));
                final String feed = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.UnRead.KEY_FEED_URL));
                Logger.i(TAG, "id(" + id + ") url(" + url + ") feed(" + feed + ")");
            } while (c.moveToNext());
        } else {
            Logger.w(TAG, "cannot move to first");
        }
        c.close();
    }

}
