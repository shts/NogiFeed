package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.models.Entry;
import android.util.Log;

import java.io.File;

public class SdCardUtils {

    private static final String TAG = SdCardUtils.class.getSimpleName();

    private static final String[] EXTENTION = {
            ".png", ".jpeg", ".jpg",
    };

    private static final String[] MIMETYPE = {
            "image/png", "image/jpg", "image/jpeg"
    };

    private SdCardUtils() {
    }

    /**
     * Reflect image to gallery.
     * @param context application context.
     * @param file file object.
     * @return
     */
    public static boolean scanFile(Context context, File file) {
        MediaScannerConnection.scanFile(
                context, new String[] { file.getAbsolutePath() }, MIMETYPE,
                    new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("MediaScannerConnection", "Scanned " + path + ":");
                        Log.d("MediaScannerConnection", "-> uri=" + uri);
                    }
                });
        return true;
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
        //return addSuffix(path);
    }

    private static String addSuffix(final String path) {

        String tempPath;
        int counter = 0;
        do {
            tempPath = path;
            tempPath = tempPath + "_" + counter;
            counter++;

        } while (exist(tempPath));

        if (!hasExtension(tempPath)) {
            tempPath += ".jpeg";
        }

        Logger.d(TAG, "getDownloadFilePath() : file path(" + tempPath + ")");
        return tempPath;
    }

    private static boolean hasExtension(String path) {
        for (String suf : EXTENTION) {
            if (path.endsWith(suf)) return true;
        }
        return false;
    }

    private static boolean exist(String path) {
        File file = new File(path);
        boolean exists = file.exists();
        Logger.d(TAG, "getDownloadFilePath() : file path(" + path + ") exists(" + exists + ")");
        return exists;
    }

    private static String getDownloadFilePath() {
        File pathExternalPublicDir =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
        // Downloadフォルダーのパス
        String dir = pathExternalPublicDir.getPath();
        return dir;
    }
}
