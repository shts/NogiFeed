package shts.jp.android.nogifeed.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.CONTENT_URI;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.KEY_ID;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.KEY_LINK;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.sProjection;

public class Favorite {

    private static final String TAG = Favorite.class.getSimpleName();

    private Favorite() {}

    public static void set(Context context, String link, boolean favorite) {
        Logger.v(TAG, "req fav : link(" + link + ") favorite(" + favorite + ")");
        if (favorite) {
            favorite(context, link);
        } else {
            unFavorite(context, link);
        }
    }

    private static void favorite(Context context, String link) {
        Logger.v(TAG, "favorite : link(" + link + ")");
        if (exist(context, link)) {
            Logger.w(TAG, "link already exist. link(" + link + ")");
            return;
        }
        final ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LINK, link);
        cr.insert(CONTENT_URI, cv);
    }

    private static void unFavorite(Context context, String link) {
        Logger.v(TAG, "unFavoriteLink : link(" + link + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_LINK + "=?";
        String[] selectionArgs = { link };

        int result = cr.delete(CONTENT_URI, selection, selectionArgs);
        if (result <= 0)  {
            Logger.w(TAG, "failed to delete link. link(" + link + ")");
        }
    }

    public static boolean exist(Context context, String link/* feed url*/) {
        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_LINK + "=?";
        String[] selectionArgs = { link };
        Logger.d(TAG, "alreadyExist : link " + link);

        Cursor c = cr.query(CONTENT_URI, sProjection, selection, selectionArgs, null);
        if (c == null) {
            Logger.d(TAG, "cursor is null. because of use favorite function first time");
            return false;
        }
        if (c.moveToFirst()) {
            do {
                String alreadyLink = c.getString(c.getColumnIndexOrThrow(KEY_LINK));
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

    public static ArrayList<String> all(Context context) {
        ArrayList<String> linkList = new ArrayList<>();
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, sProjection,
                null, null, null);
        if (c == null) {
            return linkList;
        }
        if (c.moveToFirst()) {
            do {
                String link = c.getString(c.getColumnIndexOrThrow(KEY_LINK));
                linkList.add(link);
            } while (c.moveToNext());
            c.close();
        } else {
            Logger.e(TAG, "failed to moveToFirst().");
            c.close();
        }
        return linkList;
    }

    public static void dump(Context context) {
        Logger.w(TAG, toString(allForDump(context)));
    }

    private static ArrayList<String> allForDump(Context context) {
        ArrayList<String> linkList = new ArrayList<>();
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, sProjection,
                null, null, null);
        if (c == null) {
            return linkList;
        }
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
                String link = c.getString(c.getColumnIndexOrThrow(KEY_LINK));
                linkList.add("id(" + id + ") link(" + link + ")");
            } while (c.moveToNext());
            c.close();
        } else {
            Logger.e(TAG, "failed to moveToFirst().");
            c.close();
        }
        return linkList;
    }

    private static String toString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
