package android.shts.jp.nogifeed.services;

import android.content.Context;
import android.shts.jp.nogifeed.api.RawImageDownloadClient;
import android.shts.jp.nogifeed.api.ThumbnailDownloadClient;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.NetworkUtils;
import android.shts.jp.nogifeed.utils.StringUtils;

import java.util.List;

public class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getSimpleName();

    private ImageDownloader() {}

    /**
     * Get all image of entry.
     * @param context application context.
     * @param entry entry.
     * @return if true execute download.
     */
    public static boolean download(final Context context, final  Entry entry) {

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (entry == null) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        // get thumbnail image urls from blog HTML.
        final List<String> thumbnailImageUrls
                = StringUtils.getThumbnailImageUrls(entry.content, 0);
        // download thumbnail image.
        ThumbnailDownloadClient.get(context, thumbnailImageUrls, entry);

        // get raw image urls from blog HTML.
        final List<String> rawImagePageUrls
                = StringUtils.getRawImagePageUrls(entry.content, 0);
        // download raw image.
        RawImageDownloadClient.get(context, rawImagePageUrls, entry);
        return true;
    }
}
