package shts.jp.android.nogifeed.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientFinishListener;
import shts.jp.android.nogifeed.utils.NetworkUtils;

public class AsyncRssClient {

    private static final String TAG = AsyncRssClient.class.getSimpleName();

    private AsyncRssClient() {}

    private static AsyncHttpClient sClient = new AsyncHttpClient();

    public static boolean read(final Context context, String url, final RssClientFinishListener listener) {
        List<String> urls = new ArrayList<String>();
        urls.add(url);
        return read(context, urls, listener);
    }

    public static boolean read(final Context context, List<String> urls, final RssClientFinishListener listener) {

        if (listener == null) {
            Logger.e(TAG, "cannot execute because of RssClientListener is null.");
            return false;
        }

        if (!NetworkUtils.isConnected(context) || NetworkUtils.isAirplaneModeOn(context)) {
            Logger.e(TAG, "cannot execute because of network disconnected.");
            listener.onFinish(null);
            return false;
        }

        if (urls == null || urls.isEmpty()) {
            Logger.e(TAG, "cannot execute because of url is null or length 0.");
            listener.onFinish(null);
            return false;
        }
        // set request URL size
        listener.setCounterSize(urls.size());

        for (String url : urls) {
            sClient.get(url, listener);
        }

        return true;
    }

}
