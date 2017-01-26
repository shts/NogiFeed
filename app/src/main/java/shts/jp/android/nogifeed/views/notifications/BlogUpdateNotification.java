package shts.jp.android.nogifeed.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.utils.PreferencesUtils;
import shts.jp.android.nogifeed.views.transformations.CircleTransformation;

public class BlogUpdateNotification extends NotificationWithId {

    private static final String TAG = BlogUpdateNotification.class.getSimpleName();
    public static final String KEY = BlogUpdateNotification.class.getSimpleName();

    /** Notification id */
    private static final String NOTIFICATION_ID_KEY = "pref_key_blog_update_notification_id";
    /** Notification idのデフォルト値 */
    private static final int DEFAULT_NOTIFICATION_ID = 1000;

    /** ブログ更新通知可否設定 */
    public static final int RES_ID_NOTIFICATION_ENABLE = R.string.setting_enable_blog_notification_key;
    /** ブログ更新通知制限設定(お気に入りメンバーのみ通知する設定) */
    public static final int RES_ID_NOTIFICATION_RESTRICTION_ENABLE = R.string.setting_enable_blog_notification_restriction_key;

    private static final CircleTransformation TRANSFORMATION = new CircleTransformation();

    private Context context;

    public BlogUpdateNotification(Context context) {
        super(context);
        this.context = context;
    }

    public void show(Entry entry) {
        // ブログ更新通知可否設定
        if (!enable()) {
            Logger.d(TAG, "do not show notification because of disable from SettingsFragment");
            return;
        }
        // ブログ更新通知制限設定
        if (restrict()) {
            // 押しメンの場合false, 押しメンでない場合trueを返却する
            if (!Favorites.exist(context, entry.getMemberId())) {
                Logger.d(TAG, "do not show restricted because of disable from SettingsFragment");
                return;
            }
        }

        Intent intent = BlogActivity.getStartIntent(context, entry);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final int notificationId = nextId();

        PendingIntent contentIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_blog_update);
        views.setTextViewText(R.id.title, entry.getTitle());
        views.setTextViewText(R.id.text, entry.getMemberName());
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setContent(views)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        ((NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);

        notified(notificationId);

        if (!TextUtils.isEmpty(entry.getMemberImageUrl())) {
            Picasso.with(context).load(entry.getMemberImageUrl())
                    .transform(TRANSFORMATION).into(views, R.id.icon, notificationId, notification);
        }
    }

    /** ブログ更新通知可否判定 */
    private boolean enable() {
        final String key = context.getResources().getString(RES_ID_NOTIFICATION_ENABLE);
        return PreferencesUtils.getBoolean(context, key, true);
    }

    /** ブログ更新通知制限判定(推しメンのみ通知する設定) */
    private boolean restrict() {
        final String key = context.getResources().getString(RES_ID_NOTIFICATION_RESTRICTION_ENABLE);
        return PreferencesUtils.getBoolean(context, key, true);
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
