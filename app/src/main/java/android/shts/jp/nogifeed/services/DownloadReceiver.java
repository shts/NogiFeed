package android.shts.jp.nogifeed.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.shts.jp.nogifeed.common.Logger;

// TODO: RawImage のURLまで取得できたが、画像のダウンロードができない。
public class DownloadReceiver extends BroadcastReceiver {

    private static final String TAG = DownloadReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        Logger.d(TAG, "on download intent.");

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == -1L) {
                Logger.e(TAG, "failed to download file.");
            } else {
                //Downloader.complete(id);
                Logger.d(TAG, "on download complete.");
            }

        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {

        } else if (DownloadManager.ACTION_VIEW_DOWNLOADS.equals(action)) {

        }

    }
}
