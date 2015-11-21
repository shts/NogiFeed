package shts.jp.android.nogifeed.models.legacy;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Member;

import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.CONTENT_URI;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_ARTICLE_URL;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_FEED_URL;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_ID;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_IMAGE_URL;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_NAME;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.KEY_WIDGET_ID;
import static shts.jp.android.nogifeed.providers.NogiFeedContent.ProfileWidget.sProjection;

public class ProfileWidget {

    private static final String TAG = ProfileWidget.class.getSimpleName();

    public int id;
    public int widgetId;
    public String name;
    public String imageUrl;
    public String articleUrl;
    public String feedUrl;

    private ProfileWidget(int id, int widgetId, String name,
                          String imageUrl, String articleUrl, String feedUrl) {
        this.id = id;
        this.widgetId = widgetId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.articleUrl = articleUrl;
        this.feedUrl = feedUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id(").append(id).append(") ");
        sb.append("widgetId(").append(widgetId).append(") ");
        sb.append("name(").append(name).append(") ");
        sb.append("imageUrl(").append(imageUrl).append(") ");
        sb.append("articleUrl(").append(articleUrl).append(") ");
        sb.append("feedUrl(").append(feedUrl).append(") ");
        return sb.toString();
    }

    public static ArrayList<ProfileWidget> all(Context context) {
        ArrayList<ProfileWidget> profileWidgets = new ArrayList<>();
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, sProjection, null, null, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return profileWidgets;
        }
        if (!c.moveToFirst()) {
            c.close();
            return profileWidgets;
        }
        do {
            final int id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
            final int appWidgetId = c.getInt(c.getColumnIndexOrThrow(KEY_WIDGET_ID));
            final String name = c.getString(c.getColumnIndexOrThrow(KEY_NAME));
            final String imageUrl = c.getString(c.getColumnIndexOrThrow(KEY_IMAGE_URL));
            final String articleUrl = c.getString(c.getColumnIndexOrThrow(KEY_ARTICLE_URL));
            final String feedUrl = c.getString(c.getColumnIndexOrThrow(KEY_FEED_URL));
            profileWidgets.add(new ProfileWidget(id, appWidgetId, name, imageUrl, articleUrl, feedUrl));

        } while (c.moveToNext());
        c.close();
        return profileWidgets;
    }

    public static void save(
            final Context context, final Member member, final int appWidgetId) {
        Logger.v(TAG, "profile widget appWidgetId(" + appWidgetId + ")");
        final ContentResolver cr = context.getContentResolver();
        final ContentValues cv = member.toContentValues();
        cv.put(KEY_WIDGET_ID, appWidgetId);
        cr.insert(CONTENT_URI, cv);
    }

    public static void delete(Context context, int[] appWidgetIds) {
        // Delete member from database
        final ContentResolver cr = context.getContentResolver();
        String selection = KEY_WIDGET_ID + "=?";

        for (int appWidgetId : appWidgetIds) {
            String[] selectionArgs = { Integer.toString(appWidgetId) };
            cr.delete(CONTENT_URI, selection, selectionArgs);
        }
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
