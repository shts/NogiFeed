package android.shts.jp.nogifeed.services;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.api.RawImageDownloadClient;
import android.shts.jp.nogifeed.api.ThumbnailDownloadClient;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.listener.DownloadCountHandler;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.NetworkUtils;
import android.shts.jp.nogifeed.utils.StringUtils;
import android.widget.Toast;

import java.util.List;

public class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getSimpleName();

    private ImageDownloader() {}

    private static final Counter sCounter = new Counter();

    /**
     * Get all image of entry.
     * @param context application context.
     * @param entry entry.
     * @return if true execute download.
     */
    public static synchronized boolean download(final Context context, final  Entry entry) {

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
                = StringUtils.getThumbnailImageUrls(entry.content, 0);

        sCounter.setThumbnailSize(thumbnailImageUrls.size());

        // download thumbnail image.
        ThumbnailDownloadClient.get(context, thumbnailImageUrls, entry, new DownloadCountHandler() {
            @Override
            public void onFinish() {
                synchronized (sCounter) {
                    if (sCounter.addThumbnailCounter()) {
                        showToast(context, R.string.toast_finish_download);
                    }
                }
            }
        });

        // get raw image urls from blog HTML.
        final List<String> rawImagePageUrls
                = StringUtils.getRawImagePageUrls(entry.content, 0);

        sCounter.setRawImageSize(rawImagePageUrls.size());

        // download raw image.
        RawImageDownloadClient.get(context, rawImagePageUrls, entry, new DownloadCountHandler() {
            @Override
            public void onFinish() {
                synchronized (sCounter) {
                    if (sCounter.addRawImagePagesCounter()) {
                        showToast(context, R.string.toast_finish_download);
                    }
                }
            }
        });
        return true;
    }

    private static void showToast(final Context context, int resId) {
        Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * control on download counter.
     */
    private static class Counter {

        // thumbnail max size
        private int mThumbnailSize = 0;
        // raw image max size
        private int mRawImageSize = 0;

        // thumbnail download counter
        private int mThumbnailCounter = 0;
        // raw image download counter
        private int mRawImagePagesCounter = 0;

        /**
         * Set thumbnail max size.
         * @param size max size.
         */
        public void setThumbnailSize(int size) {
            mThumbnailSize = size;
        }

        /**
         * Add thumbnail download counter.
         * @return true if download complete.
         */
        public boolean addThumbnailCounter() {
            mThumbnailCounter++;
            if (isCompleteThumbnail()) {
                if (isCompleteRawImages()) {
                    allComplete();
                    return true;
                }
            }
            return false;
        }

        private boolean isCompleteThumbnail() {
            return mThumbnailSize <= mThumbnailCounter;
        }

        /**
         * Set raw image max size.
         * @param size max size.
         */
        public void setRawImageSize(int size) {
            mRawImageSize = size;
        }

        /**
         * Add raw image download counter.
         * @return true if download complete.
         */
        public boolean addRawImagePagesCounter() {
            mRawImagePagesCounter++;
            if (isCompleteRawImages()) {
                if (isCompleteThumbnail()) {
                    allComplete();
                    return true;
                }
            }
            return false;
        }

        private boolean isCompleteRawImages() {
            return mRawImageSize <= mRawImagePagesCounter;
        }

        private void allComplete() {
            mThumbnailCounter = 0;
            mRawImagePagesCounter = 0;
        }
    }
}
