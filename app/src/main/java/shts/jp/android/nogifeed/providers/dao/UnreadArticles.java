package shts.jp.android.nogifeed.providers.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import shts.jp.android.nogifeed.providers.NogiFeedContent;

public class UnreadArticles extends ArrayList<UnreadArticle> {

    private UnreadArticles() {
    }

    public static UnreadArticles all(@NonNull Context context) {
        UnreadArticles unreadArticles = new UnreadArticles();
        Cursor c = context.getContentResolver().query(
                NogiFeedContent.UnRead.CONTENT_URI, null, null, null, null);
        if (c == null || !c.moveToFirst()) return unreadArticles;
        try {
            do {
                int id = c.getInt(c.getColumnIndex(NogiFeedContent.UnRead.Key.ID));
                int memberId = c.getInt(c.getColumnIndex(NogiFeedContent.UnRead.Key.MEMBER_ID));
                String url = c.getString(c.getColumnIndex(NogiFeedContent.UnRead.Key.ARTICLE_URL));
                unreadArticles.add(new UnreadArticle(id, memberId, url));
            } while (c.moveToNext());
        } finally {
            c.close();
        }
        return unreadArticles;
    }

    public static int count(@NonNull Context context, int memberId) {
        String selection = NogiFeedContent.UnRead.Key.MEMBER_ID + "=?";
        String[] selectionArgs = { String.valueOf(memberId) };

        Cursor c = context.getContentResolver().query(
                NogiFeedContent.UnRead.CONTENT_URI, null, selection, selectionArgs, null);
        if (c == null || !c.moveToFirst()) return 0;
        try {
            return c.getCount();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return 0;
    }

    /**
     * OfficialReportの記事を未読登録する
     *
     * @param context context
     * @param url     記事のurl
     */
    public static void add(@NonNull Context context, @NonNull String url) {
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.UnRead.Key.MEMBER_ID, NogiFeedContent.UnRead.Value.OFFICIAL_REPORT);
        cv.put(NogiFeedContent.UnRead.Key.ARTICLE_URL, url);
        context.getContentResolver().insert(NogiFeedContent.UnRead.CONTENT_URI, cv);
    }

    /**
     * Blogの記事を未読登録する
     *
     * @param context context
     * @param url     記事のurl
     */
    public static void add(@NonNull Context context, int memberId, @NonNull String url) {
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.UnRead.Key.MEMBER_ID, memberId);
        cv.put(NogiFeedContent.UnRead.Key.ARTICLE_URL, url);
        context.getContentResolver().insert(NogiFeedContent.UnRead.CONTENT_URI, cv);
    }

    public static void remove(@NonNull Context context, @NonNull String url) {
        String selection = NogiFeedContent.UnRead.Key.ARTICLE_URL + "=?";
        String[] selectionArgs = {url};
        context.getContentResolver().delete(NogiFeedContent.UnRead.CONTENT_URI, selection, selectionArgs);
    }

    public static boolean exist(@NonNull Context context, @NonNull String url) {
        String selection = NogiFeedContent.UnRead.Key.ARTICLE_URL + "=?";
        String[] selectionArgs = {url};

        Cursor c = context.getContentResolver().query(
                NogiFeedContent.UnRead.CONTENT_URI,
                NogiFeedContent.UnRead.sProjection,
                selection, selectionArgs, null);
        if (c == null || !c.moveToFirst()) return false;
        try {
            return c.getCount() == 1;
        } finally {
            c.close();
        }
    }

}
