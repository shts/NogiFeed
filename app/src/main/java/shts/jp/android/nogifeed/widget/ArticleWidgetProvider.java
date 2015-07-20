package shts.jp.android.nogifeed.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.ConfigureActivity;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.providers.NogiFeedContent;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.views.transformations.CircleTransformation;

public class ArticleWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ArticleWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Logger.v(TAG, "onUpdate");

        for (int appWidgetId : appWidgetIds) {
            Member member = getMemberFrom(context, appWidgetId);
            if (member == null) {
                Logger.v(TAG, "member is null");
                continue;
            }
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
            remoteViews.setTextViewText(R.id.text, member.name);
            remoteViews.setOnClickPendingIntent(R.id.image, getOnClickIntent(context, member, appWidgetId));
            Picasso.with(context).load(member.profileImageUrl).transform(new CircleTransformation())
                    .into(remoteViews, R.id.image, new int[] { appWidgetId });
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private Member getMemberFrom(Context context, int id) {
        Logger.v(TAG, "getMemberFrom : id(" + id + ")");
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

    private PendingIntent getOnClickIntent(Context context, Member member, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(ProfileWidgetIntentReceiver.CLICK); //TODO:
        intent.putExtra(Member.KEY, member);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void update(Context context, int appWidgetId) {
        Member member = ConfigureActivity.getMember(appWidgetId);
        if (member == null) {
            Logger.w(TAG, "member is null");
            return;
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
        remoteViews.setTextViewText(R.id.text, member.name);
        remoteViews.setOnClickPendingIntent(R.id.image, null/*getOnClickIntent(context)*/);
        Picasso.with(context).load(member.profileImageUrl).transform(new CircleTransformation())
                .into(remoteViews, R.id.image, new int[] { appWidgetId });
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        DataStoreUtils.saveWidgetProfileMember(context, member, appWidgetId);
    }
}
