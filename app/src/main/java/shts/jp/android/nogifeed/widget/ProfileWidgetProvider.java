package shts.jp.android.nogifeed.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.ConfigureActivity;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.models.ProfileWidget;
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
            List<ProfileWidget> profileWidgets = ProfileWidget.all();
            for (ProfileWidget widget : profileWidgets) {
                updateWidget(context, widget);
            }
        }

        private void updateWidget(Context context, ProfileWidget widget) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            // unread badge
            final int unReadCount = NotYetRead.count(widget);
            if (unReadCount <= 0) {
                remoteViews.setViewVisibility(R.id.counter, View.GONE);
            } else {
                remoteViews.setViewVisibility(R.id.counter, View.VISIBLE);
                remoteViews.setTextViewText(R.id.counter, String.valueOf(unReadCount));
            }
            appWidgetManager.updateAppWidget(widget.getWidgetId(), remoteViews);
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
        ProfileWidget.delete(appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Logger.v(TAG, "onUpdate(Context, AppWidgetManager, int[]) in : appWidgetIds("
                + intArrayToString(appWidgetIds) + ")");

        for (int appWidgetId : appWidgetIds) {
            ProfileWidget profileWidget = ProfileWidget.getReference(appWidgetId);
            if (profileWidget == null) {
                Logger.v(TAG, "profileWidget is null");
                continue;
            }
            Member member = Member.getReference(profileWidget.getMemberObjectId());
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

    private static PendingIntent getOnClickIntent(Context context, Member member, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(ProfileWidgetIntentReceiver.CLICK);
        intent.putExtra(Member.KEY, member.getObjectId());
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
        ProfileWidget.saveLocal(appWidgetId, member);
    }

    private static void updateWidgetView(Context context, Member member, int appWidgetId) {
        Logger.d(TAG, "updateWidgetView(Context, Member, int) in : member("
            + member.toString() + ") appWidgetId(" + appWidgetId + ")");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
        remoteViews.setTextViewText(R.id.text, member.getNameMain());
        remoteViews.setOnClickPendingIntent(R.id.image, getOnClickIntent(context, member, appWidgetId));
        PicassoHelper.loadAndCircleTransform(
                context, member.getProfileImageUrl(), remoteViews, R.id.image, appWidgetId);

        // unread badge
        final int unReadCount = NotYetRead.count(member);
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
