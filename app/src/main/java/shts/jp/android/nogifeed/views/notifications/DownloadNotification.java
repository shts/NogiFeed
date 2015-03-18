package shts.jp.android.nogifeed.views.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.utils.PreferencesUtils;

public class DownloadNotification {

    private static final String TAG = DownloadNotification.class.getSimpleName();

    private static final int DEFAULT_NOTIFICATION_ID = 2000;
    private static final String NOTIFICATION_ID_KEY = "pref_key_download_notification_id";

    private final Context mContext;

    private NotificationCompat.Builder mNotification = null;
    private NotificationManager mNotificationManager = null;

    private int mCounter = 0;
    private int mMaxCounter = 0;
    private final int mNotificationId;

    public DownloadNotification(Context context, int targetSize) {
        this(context);
        mMaxCounter = targetSize;
    }

    public DownloadNotification(Context context) {
        mNotificationId = getNotificationId(context);
        mContext = context;
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
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

        mNotificationManager.notify(mNotificationId, mNotification.build());
    }

    /**
     * update notification progress.
     */
    public void updateProgress(String path) {
        mNotification.setProgress(mMaxCounter, mCounter++, false);
        mNotification.setOngoing(true);
        mNotificationManager.notify(mNotificationId, mNotification.build());

        if (mMaxCounter <= mCounter) {
            finishProgress(path);
        }
    }

    private void finishProgress(String path) {
        mCounter = 0;
        mNotification.setSmallIcon(R.drawable.ic_notification);
        Resources res = mContext.getResources();
        mNotification.setTicker(res.getString(R.string.notify_finish_download_ticker));
        mNotification.setContentTitle(res.getString(R.string.notify_finish_download_title));
        mNotification.setContentText(res.getString(R.string.notify_finish_download_text));
        mNotification.setContentIntent(getPendingIntentFrom(path));
        mNotification.setProgress(0, 0, false);
        mNotification.setOngoing(false);
        mNotification.setAutoCancel(true);
        mNotificationManager.notify(mNotificationId, mNotification.build());
        notified(mContext, mNotificationId);
    }

    private PendingIntent getPendingIntentFrom(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setData(Uri.parse(path));
        return PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static int getNotificationId(Context context) {
        return PreferencesUtils.getInt(context, NOTIFICATION_ID_KEY, DEFAULT_NOTIFICATION_ID);
    }

    private static void notified(Context context, int id) {
        if (++id >= 2999) {
            id = DEFAULT_NOTIFICATION_ID;
        }
        PreferencesUtils.setInt(context, NOTIFICATION_ID_KEY, id);
    }

}
