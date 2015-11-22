package shts.jp.android.nogifeed.api;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.utils.NetworkUtils;
import shts.jp.android.nogifeed.utils.SdCardUtils;

public class ImageDownloadClient {

    private static final String TAG = ImageDownloadClient.class.getSimpleName();

    private static AsyncHttpClient client = new AsyncHttpClient();

    private ImageDownloadClient() {}

    public static boolean get(final Context context,
                              final List<String> imageUrls,
                              final DownloadFinishListener listener) {
        if (listener == null) {
            Logger.w(TAG, "cannot download listener is null.");
            return false;
        }
        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            Logger.w(TAG, "cannot download because of imageUrls is null.");
            return false;
        }
        for (String imageUrl : imageUrls) {
            get(context, imageUrl, listener);
        }
        return true;
    }

    public static boolean get(final Context context,
                              final String imageUrl,
                              final DownloadFinishListener listener) {
        if (listener == null) {
            Logger.w(TAG, "cannot download listener is null.");
            return false;
        }
        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }
        if (TextUtils.isEmpty(imageUrl)) {
            Logger.w(TAG, "cannot download because of imageUrls is null.");
            return false;
        }
        final File file = new File(SdCardUtils.getDownloadFilePath(imageUrl));
        client.get(imageUrl, new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Logger.e(TAG, "failed to download. url(" + imageUrl + ")");
                listener.onFailure();
                Toast.makeText(context, R.string.toast_failed_download, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                listener.onSuccess(context, file);
                Toast.makeText(context, R.string.toast_download_complete, Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
}
