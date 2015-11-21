package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
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
     * @param listener callback for scan completed.
     */
    public static void scanFile(Context context, File file,
                                MediaScannerConnection.OnScanCompletedListener listener ) {
        MediaScannerConnection.scanFile(
                context, new String[] { file.getAbsolutePath() }, MIMETYPE, listener);
    }

    /**
     * Get file absolute path.
     * @param entry entry object.
     * @return file absolute path.
     */
    public static String getDownloadFilePath(Entry entry, int counter, String imageType) {
        return getDownloadFilePath() + File.separator + getFileName(entry.getBlogUrl(), counter, imageType);
    }

    /**
     * Get saved file name.
     * @param articleUrl article url. ex) http://blog.nogizaka46.com/mai.shiraishi/2015/07/024287.php
     * @return file name. ex) mai_shiraishi_2015_07_024287_t_0.jpeg [saved thumbnail]
     */
    private static String getFileName(String articleUrl, int counter, String imageType) {
        String fileName;
        final String[] names = StringUtils.getFullNameFromArticleUrl(articleUrl);
        if (names.length == 2) {
            fileName = names[0] + "_" + names[1] + "_"
                    + getFormattedDate(articleUrl) + "_" + imageType + "_" + counter + "_";
        } else {
            fileName = names[0] + "_"
                    + getFormattedDate(articleUrl) + "_" + imageType + "_" + counter + "_";
        }

        if (!hasExtension(fileName)) {
            fileName += ".jpeg";
        }

        return fileName;
    }

    /**
     * Get formatted date text.
     * @param articleUrl ex) http://blog.nogizaka46.com/mai.shiraishi/2015/07/024287.php
     * @return formatted date text. ex) 2015_07_024287
     */
    private static String getFormattedDate(String articleUrl) {
        String[] dates = articleUrl.replace(".php", "").split("/");
        String year = dates[dates.length - 3];
        String month = dates[dates.length - 2];
        String daytime = dates[dates.length - 1];
        return year + "_" + month + "_" + daytime;
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
     * @return download dir path.
     */
    private static String getDownloadFilePath() {
        File pathExternalPublicDir =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
        return pathExternalPublicDir.getPath();
    }
}
