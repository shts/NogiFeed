package shts.jp.android.nogifeed.services;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.views.notifications.DownloadNotification;

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
    public static synchronized boolean download(final Context context, final shts.jp.android.nogifeed.models.Entry entry) {

        if (!shts.jp.android.nogifeed.utils.NetworkUtils.enableNetwork(context)) {
            shts.jp.android.nogifeed.common.Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (entry == null) {
            shts.jp.android.nogifeed.common.Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        showToast(context, R.string.toast_start_download);

        sCounter.initialize(context);

        // get thumbnail image urls from blog HTML.
        final List<String> thumbnailImageUrls
                = shts.jp.android.nogifeed.utils.JsoupUtils.getThumbnailImageUrls(entry.content, 0);
        sCounter.setThumbnailSize(thumbnailImageUrls.size());

        // get raw image urls from blog HTML.
        final List<String> rawImagePageUrls
                = shts.jp.android.nogifeed.utils.JsoupUtils.getRawImagePageUrls(entry.content, 0);
        sCounter.setRawImageSize(rawImagePageUrls.size());

        if (!sCounter.isEnableCounter()) {
            showToast(context, R.string.toast_failed_download);
            return false;
        }

        // download thumbnail image.
        shts.jp.android.nogifeed.api.ThumbnailDownloadClient.get(context, thumbnailImageUrls, entry, new shts.jp.android.nogifeed.listener.DownloadCountHandler() {
            @Override
            public void onFinish() {
                synchronized (sCounter) {
                    if (sCounter.addThumbnailCounter()) {
                        showToast(context, R.string.toast_finish_download);
                    }
                }
            }
        });

        // download raw image.
        shts.jp.android.nogifeed.api.RawImageDownloadClient.get(context, rawImagePageUrls, entry, new shts.jp.android.nogifeed.listener.DownloadCountHandler() {
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

        private DownloadNotification mNotification;

        public void initialize(final Context context) {
            mNotification = new DownloadNotification(context);
            mNotification.startProgress();
        }

        public boolean isEnableCounter() {
            if (mThumbnailSize + mRawImageSize <= 0) {
                mNotification.failedProgress();
                return false;
            }
            return true;
        }

        /**
         * Set thumbnail max size.
         * @param size max size.
         */
        public void setThumbnailSize(int size) {
            mThumbnailSize = size;
            mNotification.addMaxSize(size);
        }

        /**
         * Add thumbnail download counter.
         * @return true if download complete.
         */
        public boolean addThumbnailCounter() {
            mThumbnailCounter++;
            mNotification.updateProgress();
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
            mNotification.addMaxSize(size);
        }

        /**
         * Add raw image download counter.
         * @return true if download complete.
         */
        public boolean addRawImagePagesCounter() {
            mRawImagePagesCounter++;
            mNotification.updateProgress();
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
