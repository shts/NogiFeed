package shts.jp.android.nogifeed.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import shts.jp.android.nogifeed.activities.NewsBrowseActivity;
import shts.jp.android.nogifeed.entities.News;

public class NewsUpdateNotification extends NotificationWithId {

    private static final String TAG = NewsUpdateNotification.class.getSimpleName();

    /** Notification id */
    private static final String NOTIFICATION_ID_KEY = "pref_key_news_update_notification_id";
    /** Notification idのデフォルト値 */
    private static final int DEFAULT_NOTIFICATION_ID = 3000;

    public NewsUpdateNotification(Context context) {
        super(context);
    }

    public void show(News news) {
        Intent intent = NewsBrowseActivity.getStartIntent(context, news);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final int notificationId = nextId();

        PendingIntent contentIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setSmallIcon(news.getNewsType().getIconResource())
                .setAutoCancel(true)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        ((NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);

        notified(notificationId);
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
