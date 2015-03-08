package shts.jp.android.nogifeed.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.utils.UrlUtils;
import shts.jp.android.nogifeed.views.transformations.CircleTransformation;

public class BlogUpdateNotification {

    private static final String TAG = BlogUpdateNotification.class.getSimpleName();
    public static final String KEY = BlogUpdateNotification.class.getSimpleName();

    private static final int NOTIFICATION_ID = 1200;

    public static void show(final Context context, final String url,
                            final String title, final String author) {
        Log.d(TAG, "url(" + url + ") title(" + title
                + ") author(" + author + ")");

        Intent intent = new Intent(context, BlogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY, url);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_blog_update);
        views.setTextViewText(R.id.title, title);
        views.setTextViewText(R.id.text, author);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setContent(views)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .build();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);

        String profileImageUrl = UrlUtils.getImageUrlFromArticleUrl(url);
        if (!TextUtils.isEmpty(profileImageUrl)) {
            Picasso.with(context).load(profileImageUrl)
                    .transform(new CircleTransformation()).into(views, R.id.icon, NOTIFICATION_ID, notification);
        }
    }
}
