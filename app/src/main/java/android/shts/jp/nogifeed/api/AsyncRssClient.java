package android.shts.jp.nogifeed.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.AtomRssParser;
import android.text.TextUtils;

import com.loopj.android.http.*;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AsyncRssClient {

    private static final String TAG = "AsyncRssClient";

    public static void read(Context context, String url, final RssClientListener listener) {

        if (!isConnected(context) || isAirplaneModeOn(context)) {
            Logger.e(TAG, "cannot execute because of network disconnected.");
            listener.onFailure(0, null, null, null);
            return;
        }

        if (listener == null) {
            Logger.e(TAG, "cannot execute because of RssClientListener is null.");
            listener.onFailure(0, null, null, null);
            return;
        }

        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "cannot execute because of url is null or length 0.");
            listener.onFailure(0, null, null, null);
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

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if( ni != null ){
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @param context
     * @return true if enabled.
     */
    private static boolean isAirplaneModeOn(Context context) {

        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;

    }

}
