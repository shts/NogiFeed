package android.shts.jp.nogifeed.api;

import android.content.Context;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.listener.DownloadCountHandler;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.NetworkUtils;
import android.shts.jp.nogifeed.utils.SdCardUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.List;

public class ThumbnailDownloadClient {

    private static final String TAG = ThumbnailDownloadClient.class.getSimpleName();

    private ThumbnailDownloadClient() {}

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static boolean get(final Context context, final List<String> imageUrls,
                              final Entry entry, final DownloadCountHandler handler) {

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            Logger.w(TAG, "cannot download because of imageUrls is null.");
            return false;
        }

        if (entry == null) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        if (handler == null) {
            Logger.w(TAG, "cannot download handler is null.");
            return false;
        }

        for (int i = 0; i < imageUrls.size(); i++) {

            final String imageUrl = imageUrls.get(i);
            final File file = new File(SdCardUtils.getDownloadFilePath(entry, i, "t"));

            client.get(imageUrl, new FileAsyncHttpResponseHandler(file) {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    Logger.e(TAG, "failed to download. url(" +  imageUrl + ")");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    SdCardUtils.scanFile(context, file);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    handler.onFinish();
                }
            });
        }

        return true;
    }
}
