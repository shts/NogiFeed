package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.shts.jp.nogifeed.R;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class TrackerUtils {

    private static Tracker sTracker;

    private TrackerUtils() {}

    public static synchronized Tracker getTracker(Context context) {
        if (sTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            sTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return sTracker;
    }

    public static void sendTrack(Context context, String screenName) {
        Tracker tracker = getTracker(context);
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    /**
     * send tracker
     * @param context
     * @param category Activity name or Fragment name.
     * @param action user action. (onclick)
     * @param label action detail.
     */
    public static void sendTrack(Context context, String category, String action, String label) {
        Tracker tracker = getTracker(context);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
