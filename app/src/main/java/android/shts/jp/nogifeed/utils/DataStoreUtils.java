package android.shts.jp.nogifeed.utils;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.shts.jp.nogifeed.providers.NogiFeedContent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataStoreUtils {

    private static final String TAG = DataStoreUtils.class.getSimpleName();

    public static void favorite(Context context, String link, boolean favorite) {
        //Log.v(TAG, "req fav : link(" + link + ") favorite(" + favorite + ")");
        if (favorite) {
            favorite(context, link);
        } else {
            unFavoriteLink(context, link);
        }
    }

    private static void favorite(Context context, String link) {
        if (alreadyExist(context, link)) {
            //Log.w(TAG, "link already exist. link(" + link + ")");
            return;
        }
        final ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.Favorite.KEY_LINK, link);
        cr.insert(NogiFeedContent.Favorite.CONTENT_URI, cv);
    }

    private static void unFavoriteLink(Context context, String link) {
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.Favorite.KEY_LINK + "=?";
        String[] selectionArgs = { link };
        int result = cr.delete(NogiFeedContent.Favorite.CONTENT_URI, selection, selectionArgs);

        if (result <= 0)  {
            //Log.e(TAG, "failed to delete link. link(" + link + ")");
        }
    }

    public static boolean alreadyExist(Context context, String link) {
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.Favorite.KEY_LINK + "=?";
        String[] selectionArgs = { link };

        Cursor c = cr.query(NogiFeedContent.Favorite.CONTENT_URI, NogiFeedContent.Favorite.sProjection,
                selection, selectionArgs, null);
        if (c == null) {
            //Log.d(TAG, "cursor is null. because of use favorite function first time");
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
            //Log.e(TAG, "alreadyExist : failed to moveToFirst().");
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
            //Log.e(TAG, "failed to moveToFirst().");
            c.close();
        }
        return links;
    }

}
