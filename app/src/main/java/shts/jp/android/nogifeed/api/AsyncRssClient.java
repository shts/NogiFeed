package shts.jp.android.nogifeed.api;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.*;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientListener;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.utils.AtomRssParser;
import shts.jp.android.nogifeed.utils.NetworkUtils;

public class AsyncRssClient {

    private static final String TAG = AsyncRssClient.class.getSimpleName();

    private AsyncRssClient() {}

    private static AsyncHttpClient sClient = new AsyncHttpClient();

    public static boolean read(final Context context, String url, final RssClientListener listener) {

        if (listener == null) {
            Logger.e(TAG, "cannot execute because of RssClientListener is null.");
            return false;
        }

        if (!NetworkUtils.isConnected(context) || NetworkUtils.isAirplaneModeOn(context)) {
            Logger.e(TAG, "cannot execute because of network disconnected.");
            listener.onFailure(0, null, null, null);
            return false;
        }

        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "cannot execute because of url is null or length 0.");
            listener.onFailure(0, null, null, null);
            return false;
        }

        sClient.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO: this is ui thread. should worker thread.
                InputStream is = new ByteArrayInputStream(responseBody);
                // TODO: is parse method thread safe?
                Entries entries = AtomRssParser.parse(is);
                listener.onSuccess(statusCode, headers, entries);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.onFailure(statusCode, headers, responseBody, error);
            }
        });

        return true;
    }

}
