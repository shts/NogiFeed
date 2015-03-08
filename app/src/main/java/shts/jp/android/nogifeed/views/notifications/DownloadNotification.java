package shts.jp.android.nogifeed.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import shts.jp.android.nogifeed.R;

public class DownloadNotification {

    private static final String TAG = DownloadNotification.class.getSimpleName();

    private static final int NOTIFICATION_CLICK = 0;
    private static final int NOTIFICATION_ID = 1000;

    private final Context mContext;

    private NotificationCompat.Builder mNotification = null;
    private NotificationManager mNotificationManager = null;

    private int mCounter = 0;
    private int mMaxCounter = 0;

    public DownloadNotification(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * add counter
     * @param size size.
     */
    public void addMaxSize(int size) {
        mMaxCounter += size;
    }

    /**
     * start notification progress.
     */
    public void startProgress() {
        mNotification = new NotificationCompat.Builder(mContext);
        mNotification.setSmallIcon(R.drawable.ic_notification);
        Resources res = mContext.getResources();
        mNotification.setTicker(res.getString(R.string.notify_start_download_ticker));
        mNotification.setContentTitle(res.getString(R.string.notify_start_download_title));
        mNotification.setContentText(res.getString(R.string.notify_start_download_text));
        // クリック時に消去させない
        mNotification.setOngoing(true);

        mNotificationManager.notify(NOTIFICATION_ID, mNotification.build());
    }

    /**
     * update notification progress.
     */
    public void updateProgress() {
        mNotification.setProgress(mMaxCounter, mCounter++, false);
        mNotification.setOngoing(true);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification.build());

        if (mMaxCounter <= mCounter) {
            finishProgress();
        }
    }

    public void failedProgress() {
        mCounter = 0;
        mNotification.setSmallIcon(R.drawable.ic_notification);
        Resources res = mContext.getResources();
        mNotification.setTicker(res.getString(R.string.notify_failed_download_ticker));
        mNotification.setContentTitle(res.getString(R.string.notify_failed_download_title));
        mNotification.setContentText(res.getString(R.string.notify_failed_download_text));
        mNotification.setProgress(0, 0, false);
        mNotification.setOngoing(false);
        mNotification.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification.build());
    }

    private void finishProgress() {
        mCounter = 0;
        mNotification.setSmallIcon(R.drawable.ic_notification);
        Resources res = mContext.getResources();
        mNotification.setTicker(res.getString(R.string.notify_finish_download_ticker));
        mNotification.setContentTitle(res.getString(R.string.notify_finish_download_title));
        mNotification.setContentText(res.getString(R.string.notify_finish_download_text));
        mNotification.setProgress(0, 0, false);
        mNotification.setOngoing(false);
        mNotification.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification.build());
    }

    public static void show(final Context context, final PendingIntent pi) {

        // NotificationBuilderを作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pi);
        // ステータスバーに表示されるテキスト
        builder.setTicker("Ticker");

        // Notificationを開いたときに表示されるタイトル
        builder.setContentTitle("ContentTitle");
        // Notificationを開いたときに表示されるサブタイトル
        builder.setContentText("ContentText");

        // 通知するタイミング
        builder.setWhen(System.currentTimeMillis());
        // 通知時の音・バイブ・ライト
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        // タップするとキャンセル(消える)
        builder.setAutoCancel(true);

        // クリック時に消去させない
        builder.setOngoing(true);

        builder.setProgress(0 ,0, false);

        // NotificationManagerを取得
        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        // Notificationを作成して通知
        manager.notify(NOTIFICATION_CLICK, builder.build());
    }

}
