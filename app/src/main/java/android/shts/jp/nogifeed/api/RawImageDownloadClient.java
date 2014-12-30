package android.shts.jp.nogifeed.api;

import android.content.Context;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.models.RawImage;
import android.shts.jp.nogifeed.utils.NetworkUtils;
import android.shts.jp.nogifeed.utils.SdCardUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

public class RawImageDownloadClient {

    private static final String TAG = RawImageDownloadClient.class.getSimpleName();

    private RawImageDownloadClient() {}

    private static AsyncHttpClient client = new AsyncHttpClient();
    static {
        //client.setThreadPool(Executors.newSingleThreadExecutor());
    }

    public static boolean get(final Context context, final List<RawImage> rawImages, final Entry entry) {

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (rawImages == null || rawImages.isEmpty()) {
            Logger.w(TAG, "cannot download because of rawImages is null.");
            return false;
        }

        if (entry == null) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        for (int i = 0; i < rawImages.size(); i++) {

            final RawImage rawImage = rawImages.get(i);
            final File file = new File(SdCardUtils.getDownloadFilePath(entry, i, "r"));

            client.addHeader("Referer", rawImage.referer);
            client.addHeader("Cache-Control", "max-age=0");
            client.get(rawImage.url, new FileAsyncHttpResponseHandler(file) {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    client.removeHeader("Referer");
                    StringBuilder sb = new StringBuilder();
                    sb.append("statusCode : ").append(statusCode).append("\n");
                    sb.append("============================").append("\n");
                    sb.append("header :").append("\n");
                    sb.append("============================").append("\n");
                    for (Header h : headers) {
                        sb.append(h.toString() + ",").append("\n");
                    }
                    sb.append("throwable").append(throwable).append("\n");
                    Logger.e(TAG, "failed to download. referer(" + rawImage.referer + ") url(" +  rawImage.url + ")" + "\n details("
                        + sb.toString() + ")");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    Logger.e(TAG, "complete download. referer(" + rawImage.referer + ") url(" +  rawImage.url + ")");
                    client.removeHeader("Referer");
                    SdCardUtils.scanFile(context, file);
                }
            });
        }

        return true;
    }

}
