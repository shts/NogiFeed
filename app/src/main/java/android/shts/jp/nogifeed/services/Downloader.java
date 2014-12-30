package android.shts.jp.nogifeed.services;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.shts.jp.nogifeed.common.Logger;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Downloader {

    private static final String TAG = Downloader.class.getSimpleName();

    private static DownloadManager sSingleton;
    private static List<Long> sExecuteId = new ArrayList<Long>();

    private synchronized static DownloadManager getInstance(Context context) {
        if (sSingleton ==  null) {
            sSingleton = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            return sSingleton;
        } else {
            return sSingleton;
        }
    }

    public static boolean execute(Context context, List<String> imageUrls) {

        if (imageUrls == null || imageUrls.isEmpty()) {
            Logger.w(TAG, "imageUrls is invalid");
            return false;
        }

        for (String url : imageUrls) {
            long l = execute(context, url);
            if (l == -1L) {
                Logger.w(TAG, "failed to execute : url(" + url + ")");
            } else {
                synchronized (sExecuteId) {
                    sExecuteId.add(l);
                }
            }
        }
        return true;
    }

    private static long execute(Context context, String imageUrl) {

        if (TextUtils.isEmpty(imageUrl)) {
            Logger.w(TAG, "imageUrl is invalid");
            return -1L;
        }

        Uri uri = Uri.parse(imageUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Download" + System.currentTimeMillis());
        request.setDescription("Description" + System.currentTimeMillis());

        // get instance
        DownloadManager manager = getInstance(context);
        long id = manager.enqueue(request);

        return id;
    }

    public static void complete(long id) {
        synchronized (sExecuteId) {
            for (int i = 0; i < sExecuteId.size(); i++) {
                Long l = sExecuteId.get(i);
                if (l != null) {
                    if (l == id) { /* auto boxing */
                        sExecuteId.remove(i);
                        return;
                    }
                }
            }
        }
    }
}
