package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import shts.jp.android.nogifeed.models.Entry;

/**
 * Get information of sd card and download directory.
 */
public class SdCardUtils {

    private static final String TAG = SdCardUtils.class.getSimpleName();

    private static final String[] EXTENTION = {
            ".png", ".jpeg", ".jpg",
    };

    private static final String[] MIMETYPE = {
            "image/png", "image/jpg", "image/jpeg"
    };

    private SdCardUtils() { }

    /**
     * Reflect image to gallery.
     * @param context application context.
     * @param file file object.
     * @return
     */
    public static void scanFile(Context context, File file) {
        MediaScannerConnection.scanFile(
                context, new String[] { file.getAbsolutePath() }, MIMETYPE,
                    new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        shts.jp.android.nogifeed.common.Logger.d("MediaScannerConnection", "Scanned " + path + ":");
                        shts.jp.android.nogifeed.common.Logger.d("MediaScannerConnection", "-> uri=" + uri);
                    }
                });
    }

    /**
     * Get file absolute path.
     * @param entry entry object.
     * @return file absolute path.
     */
    public static String getDownloadFilePath(Entry entry, int counter, String imageType) {

        String updated = DateUtils.formatFileName(entry.updated);

        String path;
        String[] names = StringUtils.getFullNameFromArticleUrl(entry.link);
        if (names.length == 2) {
            path = getDownloadFilePath() + File.separator + names[0] + names[1]
                    + updated + imageType + "_" + counter;
        } else {
            path = getDownloadFilePath() + File.separator + names[0]
                    + updated + imageType + "_" + counter;
        }

        if (!hasExtension(path)) {
            path += ".jpeg";
        }

        return path;
    }

    /**
     * Check does have path {@link shts.jp.android.nogifeed.utils.SdCardUtils#EXTENTION}.
     * @param path path
     * @return true if path end width {@link shts.jp.android.nogifeed.utils.SdCardUtils#EXTENTION}.
     */
    private static boolean hasExtension(String path) {
        for (String suf : EXTENTION) {
            if (path.endsWith(suf)) return true;
        }
        return false;
    }

    /**
     * Get android default 'download' dir path
     * @return download die path.
     */
    private static String getDownloadFilePath() {
        File pathExternalPublicDir =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
        return pathExternalPublicDir.getPath();
    }
}
