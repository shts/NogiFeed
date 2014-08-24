package android.shts.jp.nogifeed.api;

import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.AtomRssParser;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.*;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by saitoushouta on 2014/08/24.
 */
public class AsyncRssClient {

    private static final String TAG = "AsyncRssClient";

    public static void read(String url, final RssClientListener listener) {

        // TODO: Check network enable.

        if (listener == null) {
            Log.e(TAG, "cannot execute because of RssClientListener is null.");
            return;
        }

        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "cannot execute because of url is null or length 0.");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
    }

}
