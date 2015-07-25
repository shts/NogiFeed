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
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.utils.JsoupUtils;
import shts.jp.android.nogifeed.utils.NetworkUtils;
import shts.jp.android.nogifeed.utils.SdCardUtils;

public class ThumbnailDownloadClient {

    private static final String TAG = ThumbnailDownloadClient.class.getSimpleName();

    private ThumbnailDownloadClient() {}

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static boolean get(final Context context, final List<String> imageUrls,
                              final Entry entry, final DownloadFinishListener listener) {

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

        if (entry == null || TextUtils.isEmpty(entry.content)) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        for (int i = 0; i < imageUrls.size(); i++) {

            final String imageUrl = imageUrls.get(i);
            final File file = new File(SdCardUtils.getDownloadFilePath(entry, i, "t"));

            client.get(imageUrl, new FileAsyncHttpResponseHandler(file) {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    Logger.e(TAG, "failed to download. url(" + imageUrl + ")");
                    listener.onFailure();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    listener.onSuccess(context, file);
                }

            });
        }

        return true;
    }

    public static boolean get(final Context context, final String imageUrl,
                              final Entry entry, final DownloadFinishListener listener) {

        if (listener == null) {
            Logger.w(TAG, "cannot download listener is null.");
            return false;
        }

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            Logger.w(TAG, "cannot download because of imageUrl is null.");
            return false;
        }

        if (entry == null || TextUtils.isEmpty(entry.content)) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        final int thumbnailIndex = JsoupUtils.getThumbnailIndex(entry.content, imageUrl);
        final File file = new File(SdCardUtils.getDownloadFilePath(entry, thumbnailIndex, "t"));

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
