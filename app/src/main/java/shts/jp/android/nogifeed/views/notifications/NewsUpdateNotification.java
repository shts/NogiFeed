package shts.jp.android.nogifeed.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.NewsBrowseActivity;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.utils.PreferencesUtils;

public class NewsUpdateNotification extends NotificationWithId {

    private static final String TAG = NewsUpdateNotification.class.getSimpleName();

    /** Notification id */
    private static final String NOTIFICATION_ID_KEY = "pref_key_news_update_notification_id";
    /** Notification idのデフォルト値 */
    private static final int DEFAULT_NOTIFICATION_ID = 3000;

    /** ブログ更新通知可否設定 */
    public static final int RES_ID_NOTIFICATION_ENABLE = R.string.setting_enable_news_notification_key;
    /** ブログ更新通知制限設定(お気に入りメンバーのみ通知する設定) */
    public static final int RES_ID_NOTIFICATION_RESTRICTION_ENABLE = R.string.setting_enable_news_notification_restriction_key;

    public NewsUpdateNotification(Context context) {
        super(context);
    }

    public void show(News news) {

        if (!enable(news)) { return; }

        Intent intent = NewsBrowseActivity.getStartIntent(context, news);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final int notificationId = nextId();

        PendingIntent contentIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setContentTitle(news.title)
                .setSmallIcon(news.getNewsType().getIconResource())
                .setAutoCancel(true)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        ((NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);

        notified(notificationId);
    }

    private boolean enable(News news) {

        final String enableKeyResId = context.getResources().getString(RES_ID_NOTIFICATION_ENABLE);
        final boolean enable = PreferencesUtils.getBoolean(context, enableKeyResId, true);
        if (!enable) {
            return false;
        }

        // TODO: 通知制限機能を追加する
//        final String restrictKeyResId = context.getResources().getString(RES_ID_NOTIFICATION_RESTRICTION_ENABLE);
        return true;
    }

    @Override
    public String getNotificationIdKey() {
        return NOTIFICATION_ID_KEY;
    }

    @Override
    public int getNotificationIdDefVal() {
        return DEFAULT_NOTIFICATION_ID;
    }
}
