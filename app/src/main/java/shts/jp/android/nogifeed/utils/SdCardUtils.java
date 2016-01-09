package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;

/**
 * Get information of sd card and download directory.
 */
public class SdCardUtils {

    private static final String TAG = SdCardUtils.class.getSimpleName();

    private static final String[] MIMETYPE = {
            "image/png", "image/jpg", "image/jpeg"
    };

    private SdCardUtils() { }

    /**
     * Reflect image to gallery.
     * @param context application context.
     * @param file file object.
     * @param listener callback for scan completed.
     */
    public static void scanFile(Context context, File file,
                                MediaScannerConnection.OnScanCompletedListener listener ) {
        MediaScannerConnection.scanFile(
                context, new String[] { file.getAbsolutePath() }, MIMETYPE, listener);
    }

    public static String getDownloadFilePath(String url) {
        String[] splitUrl = url.split("/");
        String fileName = splitUrl[splitUrl.length - 1];
        return getDownloadFilePath() + File.separator + "download" + File.separator + fileName;
    }

    /**
     * Get android default 'download' dir path
     * @return download dir path.
     */
    private static String getDownloadFilePath() {
        File pathExternalPublicDir =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
        return pathExternalPublicDir.getPath();
    }
}
