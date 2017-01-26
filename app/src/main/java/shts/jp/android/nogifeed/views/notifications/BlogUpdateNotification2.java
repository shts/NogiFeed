package shts.jp.android.nogifeed.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.utils.PreferencesUtils;
import shts.jp.android.nogifeed.views.transformations.CircleTransformation;

public class BlogUpdateNotification2 {

    private static final String TAG = BlogUpdateNotification.class.getSimpleName();

    private static final String NOTIFICATION_ID_KEY = "pref_key_blog_update_notification_id";
    private static final int DEFAULT_NOTIFICATION_ID = 1000;

    /** ブログ更新通知可否設定 */
    private static final int NOTIFICATION_ENABLE = R.string.setting_enable_blog_notification_key;
    /** ブログ更新通知制限設定(お気に入りメンバーのみ通知する設定) */
    private static final int NOTIFICATION_RESTRICTION_ENABLE = R.string.setting_enable_blog_notification_restriction_key;

    public static void showExecUiThread(@NonNull final Context context,
                                        @NonNull final Entry entry) {
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                show(context, entry);
            }
        });
    }

    public static void show(Context context, Entry entry) {

        final boolean isEnableNotification
                = PreferencesUtils.getBoolean(context, context.getString(NOTIFICATION_ENABLE), true);
        if (!isEnableNotification) {
            Log.d(TAG, "do not show notification because of notification disable");
            return;
        }

        if (isRestriction(context, entry.getMemberId())) {
            Log.d(TAG, "do not show notification because of notification restriction");
            return;
        }

        Intent intent = BlogActivity.getStartIntent(context, entry);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final int notificationId = getNotificationId(context);

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

        notified(context, notificationId);

        if (!TextUtils.isEmpty(entry.getMemberImageUrl())) {
            Picasso.with(context).load(entry.getMemberImageUrl())
                    .placeholder(R.drawable.kensyusei)
                    .transform(new CircleTransformation()).into(
                    views, R.id.icon, notificationId, notification);
        } else {
            views.setImageViewResource(R.id.icon, R.drawable.kensyusei);
        }
    }

    private static boolean isRestriction(Context context, int memberId) {
        final boolean isRestriction = PreferencesUtils.getBoolean(
                context, context.getString(NOTIFICATION_RESTRICTION_ENABLE), false);
        if (!isRestriction) {
            // 通知制限設定をしていない場合はそのまま通知するようfalseを返却する
            Log.d(TAG, "restriction is not setting");
            return false;
        }

        // お気に入りメンバー登録済みの場合false, お気に入りメンバー登録済みでない場合trueを返却する
        final boolean exist = Favorites.exist(context, memberId);
        Log.d(TAG, "restriction exist(" + exist + ")");
        return !exist;
    }

    private static int getNotificationId(Context context) {
        return PreferencesUtils.getInt(context, NOTIFICATION_ID_KEY, DEFAULT_NOTIFICATION_ID);
    }

    private static void notified(Context context, int id) {
        if (++id >= 1999) {
            id = DEFAULT_NOTIFICATION_ID;
        }
        PreferencesUtils.setInt(context, NOTIFICATION_ID_KEY, id);
    }

}
