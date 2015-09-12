package shts.jp.android.nogifeed.utils;

import android.os.Handler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.providers.NogiFeedContent;

public class DataStoreUtils {

    private static final String TAG = DataStoreUtils.class.getSimpleName();
    private static final Handler HANDLER = new Handler();

    public static void favorite(Context context, String link, boolean favorite) {
        Logger.v(TAG, "req fav : link(" + link + ") favorite(" + favorite + ")");
        if (favorite) {
            favorite(context, link);
        } else {
            unFavoriteLink(context, link);
        }
    }

    private static void favorite(Context context, String link) {
        Logger.v(TAG, "favorite : link(" + link + ")");
        if (alreadyExist(context, link)) {
            Logger.w(TAG, "link already exist. link(" + link + ")");
            return;
        }
        final ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.Favorite.KEY_LINK, link);
        cr.insert(NogiFeedContent.Favorite.CONTENT_URI, cv);
    }

    private static void unFavoriteLink(Context context, String link) {
        Logger.v(TAG, "unFavoriteLink : link(" + link + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.Favorite.KEY_LINK + "=?";
        String[] selectionArgs = { link };

        int result = cr.delete(NogiFeedContent.Favorite.CONTENT_URI, selection, selectionArgs);
        if (result <= 0)  {
            Logger.w(TAG, "failed to delete link. link(" + link + ")");
        }
    }

    public static boolean alreadyExist(Context context, String link) {
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.Favorite.KEY_LINK + "=?";
        String[] selectionArgs = { link };
        Logger.d(TAG, "alreadyExist : link " + link);

        Cursor c = cr.query(NogiFeedContent.Favorite.CONTENT_URI, NogiFeedContent.Favorite.sProjection,
                selection, selectionArgs, null);
        if (c == null) {
            Logger.d(TAG, "cursor is null. because of use favorite function first time");
            return false;
        }
        if (c.moveToFirst()) {
            do {
                String alreadyLink = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
                if (link.equals(alreadyLink)) {
                    return true;
                }
            } while (c.moveToNext());
            c.close();
        } else {
            Logger.w(TAG, "alreadyExist : failed to moveToFirst().");
            c.close();
            return false;
        }
        return false;
    }

//    public static String getAllFavoriteLink(Context context) {
//        String[] links = null;
//        final ContentResolver cr = context.getContentResolver();
//        Cursor c = cr.query(NogiFeedContent.Favorite.CONTENT_URI, NogiFeedContent.Favorite.sProjection,
//                null, null, null);
//        if (c.moveToFirst()) {
//            int counter = 0;
//            links = new String[c.getCount()];
//            do {
//                String link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
//                links[counter] = link;
//                counter++;
//            } while (c.moveToNext());
//            c.close();
//        } else {
//            Log.e(TAG, "failed to moveToFirst().");
//            c.close();
//        }
//        return toString(links);
//    }
//
//    private static String toString(String[] links) {
//        if (links == null) return null;
//
//        String link = null;
//        for (String s : links) {
//            Log.i(TAG, "toString : link(" + s + ")");
//            link += s + ", ";
//        }
//        return link;
//    }

    public static List<String> getAllFavoriteLink(Context context) {
        List<String> links = new ArrayList<String>();
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(NogiFeedContent.Favorite.CONTENT_URI, NogiFeedContent.Favorite.sProjection,
                null, null, null);
        if (c.moveToFirst()) {
            do {
                String link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
                links.add(link);
            } while (c.moveToNext());
            c.close();
        } else {
            Logger.w(TAG, "getAllFavoriteLink() : failed to moveToFirst().");
            c.close();
        }
        return links;
    }

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
