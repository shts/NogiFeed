package shts.jp.android.nogifeed.providers.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.providers.NogiFeedContent;

public class ProfileWidgets extends ArrayList<ProfileWidget> {

    public static ProfileWidgets all(@NonNull Context context) {
        Cursor c = context.getContentResolver().query(
                NogiFeedContent.ProfileWidget.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        ProfileWidgets profileWidgets = new ProfileWidgets();
        try {
            while (c.moveToNext()) {
                profileWidgets.add(new ProfileWidget(
                        c.getInt(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.Key.ID)),
                        c.getInt(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.Key.WIDGET_ID)),
                        c.getInt(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.Key.MEMBER_ID))));
            }
            return profileWidgets;
        } catch (Throwable throwable) {
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    public static boolean exist(@NonNull Context context,
                                @NonNull Member member) {
        String selection = NogiFeedContent.ProfileWidget.Key.MEMBER_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(member.getId()) };

        Cursor c = context.getContentResolver().query(
                NogiFeedContent.ProfileWidget.CONTENT_URI,
                NogiFeedContent.ProfileWidget.sProjection,
                selection,
                selectionArgs,
                null
        );

        if (c != null) {
            if (c.moveToFirst()) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
        return false;
    }

    public static void save(@NonNull Context context, Member member, int widgetId) {
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.ProfileWidget.Key.WIDGET_ID, widgetId);
        cv.put(NogiFeedContent.ProfileWidget.Key.MEMBER_ID, member.getId());
        context.getContentResolver().insert(
                NogiFeedContent.ProfileWidget.CONTENT_URI, cv);
    }

    public static void delete(@NonNull Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            delete(context, widgetId);
        }
    }

    public static void delete(@NonNull Context context, int widgetId) {
        String selection = NogiFeedContent.ProfileWidget.Key.WIDGET_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(widgetId) };

        context.getContentResolver().delete(
                NogiFeedContent.ProfileWidget.CONTENT_URI,
                selection,
                selectionArgs
        );
    }

}
