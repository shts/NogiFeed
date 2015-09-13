package shts.jp.android.nogifeed.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.ConfigureActivity;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Member;
import shts.jp.android.nogifeed.models.ProfileWidget;
import shts.jp.android.nogifeed.models.UnRead;
import shts.jp.android.nogifeed.providers.NogiFeedContent;
import shts.jp.android.nogifeed.utils.PicassoHelper;

public class ProfileWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ProfileWidgetProvider.class.getSimpleName();

    static class ProfileWidgetContentObserver extends ContentObserver {

        private final Context context;

        public ProfileWidgetContentObserver(Context context) {
            super(new Handler());
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            ArrayList<ProfileWidget> profileWidgets = ProfileWidget.all(context);
            for (ProfileWidget widget : profileWidgets) {
                updateWidget(context, widget);
            }
        }

        private void updateWidget(Context context, ProfileWidget widget) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            // unread badge
            final int unReadCount = UnRead.count(context, widget.feedUrl);
            Logger.d(TAG, "unReadCount(" + unReadCount + ") feedUrl(" + widget.feedUrl + ")");
            if (unReadCount <= 0) {
                remoteViews.setViewVisibility(R.id.counter, View.GONE);
            } else {
                remoteViews.setViewVisibility(R.id.counter, View.VISIBLE);
                remoteViews.setTextViewText(R.id.counter, String.valueOf(unReadCount));
            }
            appWidgetManager.updateAppWidget(widget.widgetId, remoteViews);
        }
    }

    public static void initialize(final Context context) {
        context.getContentResolver().registerContentObserver(
                NogiFeedContent.UnRead.CONTENT_URI, true, new ProfileWidgetContentObserver(context));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Logger.v(TAG, "onDeleted(Context, int[]) in : appWidgetIds("
                + intArrayToString(appWidgetIds) + ")");
        ProfileWidget.delete(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Logger.v(TAG, "onUpdate(Context, AppWidgetManager, int[]) in : appWidgetIds("
                + intArrayToString(appWidgetIds) + ")");

        for (int appWidgetId : appWidgetIds) {
            Member member = getMemberFrom(context, appWidgetId);
            if (member == null) {
                Logger.v(TAG, "member is null");
                continue;
            }
            updateWidgetView(context, member, appWidgetId);
        }
    }

    private String intArrayToString(int[] appWidgetIds) {
        if (appWidgetIds == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < appWidgetIds.length; i++) {
            sb.append("index(").append(i).append(") appWidgetId(")
                    .append(String.valueOf(appWidgetIds[i])).append(")");
        }
        return sb.toString();
    }

    private Member getMemberFrom(Context context, int id) {
        Logger.v(TAG, "getMemberFrom in : id(" + id + ")");
        final ContentResolver cr = context.getContentResolver();
        String selection = NogiFeedContent.ProfileWidget.KEY_WIDGET_ID + "=?";
        String[] selectionArgs = { Integer.toString(id) };
        Cursor c = cr.query(NogiFeedContent.ProfileWidget.CONTENT_URI,
                NogiFeedContent.ProfileWidget.sProjection, selection, selectionArgs, null);
        if (c == null) {
            Logger.w(TAG, "cursor is null");
            return null;
        }
        if (c.moveToFirst()) {
            return new Member(c);
        } else {
            Logger.w(TAG, "failed to moveToFirst");
        }
        c.close();
        return null;
    }

    private static PendingIntent getOnClickIntent(Context context, Member member, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(ProfileWidgetIntentReceiver.CLICK);
        intent.putExtra(Member.KEY, member);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void update(Context context, int appWidgetId) {
        Member member = ConfigureActivity.getMember(appWidgetId);
        if (member == null) {
            Logger.w(TAG, "member is null");
            return;
        }
        updateWidgetView(context, member, appWidgetId);

        // save new widget
        ProfileWidget.save(context, member, appWidgetId);
    }

    private static void updateWidgetView(Context context, Member member, int appWidgetId) {
        Logger.d(TAG, "updateWidgetView(Context, Member, int) in : member("
            + member.toString() + ") appWidgetId(" + appWidgetId + ")");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
        remoteViews.setTextViewText(R.id.text, member.name);
        remoteViews.setOnClickPendingIntent(R.id.image, getOnClickIntent(context, member, appWidgetId));
        PicassoHelper.loadAndCircleTransform(
                context, member.profileImageUrl, remoteViews, R.id.image, appWidgetId);

        // unread badge
        final int unReadCount = UnRead.count(context, member.feedUrl);
        if (unReadCount <= 0) {
            remoteViews.setViewVisibility(R.id.counter, View.GONE);
        } else {
            remoteViews.setViewVisibility(R.id.counter, View.VISIBLE);
            remoteViews.setTextViewText(R.id.counter, String.valueOf(unReadCount));
        }

        // update remote view
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}
