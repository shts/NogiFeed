package shts.jp.android.nogifeed.listener;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.utils.SdCardUtils;
import shts.jp.android.nogifeed.views.notifications.DownloadNotification;

public class DownloadFinishListener {

    private DownloadNotification mNotification;

    public DownloadFinishListener(Context context, int targetSize) {
        mNotification = new DownloadNotification(context, targetSize);
        mNotification.startProgress();
    }

    public void onSuccess(Context context, final File file) {
        SdCardUtils.scanFile(context, file, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Logger.d("MediaScannerConnection", "Scanned " + path + ":");
                Logger.d("MediaScannerConnection", "-> uri=" + uri);
                mNotification.updateProgress(uri);
            }
        });
    }

    public void onFailure() {
        // 一応加算してあとで警告を表示する
        mNotification.updateProgress(null);
    }

}
