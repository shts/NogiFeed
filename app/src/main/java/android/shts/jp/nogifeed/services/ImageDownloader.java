package android.shts.jp.nogifeed.services;

import android.content.Context;
import android.shts.jp.nogifeed.api.RawImageDownloadClient;
import android.shts.jp.nogifeed.api.ThumbnailDownloadClient;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.models.RawImage;
import android.shts.jp.nogifeed.utils.ArrayUtils;
import android.shts.jp.nogifeed.utils.JsoupUtils;
import android.shts.jp.nogifeed.utils.NetworkUtils;
import android.shts.jp.nogifeed.utils.SdCardUtils;
import android.shts.jp.nogifeed.utils.StringUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getSimpleName();

    private ImageDownloader() {}

    public interface ImageDownloadListener {
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file);
        public void onSuccess(int statusCode, Header[] headers, File file);
    }

    /**
     * Get all image of entry.
     * @param context application context.
     * @param entry entry.
     * @param listener image download listener.
     * @return if true execute download.
     */
    public static boolean download(final Context context, final  Entry entry, final ImageDownloadListener listener) {

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (entry == null) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        if (listener == null) {
            Logger.w(TAG, "cannot download because of listener is null.");
            return false;
        }


        final List<String> thumbnailImageUrls
                = StringUtils.getThumbnailImageUrls(entry.content, 0);
        final List<String> rawImagePageUrls
                = StringUtils.getRawImagePageUrls(entry.content, 0);

        ThumbnailDownloadClient.get(context, thumbnailImageUrls, entry);

        boolean ret = JsoupUtils.getEnableRawImageUrls(
                context, rawImagePageUrls, new JsoupUtils.GetImageUrlsListener() {

                    @Override
                    public void onSuccess(List<RawImage> rawImages) {
                        //ArrayUtils.concatenation(thumbnailImageUrls, rawImages);
//                        for (int i = 0; i < rawImages.size(); i++) {
//                            RawImage rawImage = rawImages.get(i);
//                            //String url = rawImages.get(i);
//                            Logger.i("enable url", "referer(" + rawImage.referer + ") url(" + rawImage.url + ")");
//                            File file = new File(SdCardUtils.getDownloadFilePath(entry, i));
//                            //download(context, file, url, listener);
//                            download(context, file, url, listener);
//                        }
//                        for (String url : thumbnailImageUrls) {
//                            Logger.i("getEnableRawImageUrl", "enableUrls(" + url + ")");
//                            File file = new File(SdCardUtils.getDownloadFilePath(entry));
//                            download(context, file, url, listener);
//                        }
                        RawImageDownloadClient.get(context, rawImages, entry);
                    }

                    @Override
                    public void onFailed() {
                    }
                });

        return ret;
    }

    public static void download(final Context context, File file, String imageUrl, final ImageDownloadListener listener) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(imageUrl, new FileAsyncHttpResponseHandler(file) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                listener.onFailure(statusCode, headers, throwable, file);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Logger.d(TAG, "file scan! File (" + file.getAbsolutePath() + ")");
                // ScanMedia
                SdCardUtils.scanFile(context, file);
                listener.onSuccess(statusCode, headers, file);
            }
        });
    }

}
