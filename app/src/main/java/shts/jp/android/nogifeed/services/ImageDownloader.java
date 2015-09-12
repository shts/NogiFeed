package shts.jp.android.nogifeed.services;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.api.RawImageDownloadClient;
import shts.jp.android.nogifeed.api.ThumbnailDownloadClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.entities.Entry;
import shts.jp.android.nogifeed.utils.JsoupUtils;
import shts.jp.android.nogifeed.utils.NetworkUtils;

public class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getSimpleName();

    private ImageDownloader() {}

    /**
     * Get all image of entry.
     * @param context application context.
     * @param entry entry.
     * @return if true execute download.
     */
    public static synchronized boolean downloads(final Context context, final Entry entry) {

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (entry == null) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        showToast(context, R.string.toast_start_download);

        // get thumbnail image urls from blog HTML.
        final List<String> thumbnailImageUrls
                = JsoupUtils.getThumbnailImageUrls(entry.content, 0);

        // get raw image urls from blog HTML.
        final List<String> rawImagePageUrls
                = JsoupUtils.getRawImagePageUrls(entry.content, 0);

        final int targetSize = getDownloadTargetSize(thumbnailImageUrls, rawImagePageUrls);
        DownloadFinishListener listener = new DownloadFinishListener(context, targetSize);

        // download thumbnail image.
        ThumbnailDownloadClient.get(context, thumbnailImageUrls, entry, listener);

        // download raw image.
        RawImageDownloadClient.get(context, rawImagePageUrls, entry, listener);
        return true;
    }

    private static int getDownloadTargetSize(List<String> thumbnailImageUrls, List<String> rawImagePageUrls) {
        int thumbnailSize = 0;
        int rawSize = 0;

        if (thumbnailImageUrls != null) {
            thumbnailSize = thumbnailImageUrls.size();
        }
        if (rawImagePageUrls != null) {
            rawSize = rawImagePageUrls.size();
        }
        return thumbnailSize + rawSize;
    }

    private static void showToast(final Context context, int resId) {
        Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }
}
