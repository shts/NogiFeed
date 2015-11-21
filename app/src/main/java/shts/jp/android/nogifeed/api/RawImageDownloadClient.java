package shts.jp.android.nogifeed.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.Header;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.File;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.utils.NetworkUtils;
import shts.jp.android.nogifeed.utils.SdCardUtils;

public class RawImageDownloadClient {

    private static final String TAG = RawImageDownloadClient.class.getSimpleName();

    private RawImageDownloadClient() {}

    public static boolean get(final Context context, final List<String> imageUrls,
                              final Entry entry, final DownloadFinishListener listener) {

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            Logger.w(TAG, "cannot download because of rawImages is null.");
            return false;
        }

        if (entry == null) {
            Logger.w(TAG, "cannot download because of entry is null.");
            return false;
        }

        if (listener == null) {
            Logger.w(TAG, "cannot download handler is null.");
            return false;
        }

        for (int i = 0; i < imageUrls.size(); i++) {

            String imageUrl = imageUrls.get(i);
            if (TextUtils.isEmpty(imageUrl)) {
                // 基本通らない想定
                continue;
            }

            //final File file = new File(SdCardUtils.getDownloadFilePath(entry, i, "r"));

            download(context, imageUrl, null, listener);
        }
        return true;
    }

    /*
    memo
    存在確認クライアントのヘッダにリファラを設定する.<- 不要かもしれないけど一応。
    存在確認結果のから取得したセッションIDをダウンロードクライアントのクッキーに設定する。
    ダウンロードクライアントを使用してファイルをダウンロードする。
    セッションIDをクリアする。
     */
    /**
     * download raw images.
     * @param context application context.
     * @param imageUrl raw image 'page' url.
     * @param file download target file.
     * @return true if execute.
     */
    private static boolean download(final Context context, final String imageUrl,
                                    final File file, final DownloadFinishListener listener) {

        // RawImageの存在確認
        // TODO : 連続でダウンロードするとクッキーがダウンロード時のものとあわなくなるときがあるので都度newする
        new AsyncHttpClient().get(imageUrl, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // TODO: this is ui thread. should worker thread.
                    // get enable raw image url from html.
                    //String url = JsoupUtils.getEnableRawImageUrl(new String(responseBody, "UTF-8"));
                    String url = "test";
                    if (TextUtils.isEmpty(url)) {
                        Logger.w(TAG, "failed to get enable url from HTML.");
                        listener.onFailure();
                        return;
                    }

                    // get session id and set it cookie.
                    PersistentCookieStore cookieStore = getCoockieStore(context, headers);
                    if (cookieStore == null) {
                        Logger.w(TAG, "failed to set session id at cookie");
                        listener.onFailure();
                        return;
                    }
                    // TODO: download should static
                    // staticで保持しているとクッキーを削除する手間があるので都度newする。
                    // できればstaticで保持できるようなスレッド安全かつクッキーが正しく取得できる仕組みを作成する。
                    AsyncHttpClient downloadClient = new AsyncHttpClient();
                    downloadClient.setCookieStore(cookieStore);
                    downloadClient.get(url, new FileAsyncHttpResponseHandler(file) {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            // TODO: this is ui thread. should worker thread.
                            listener.onSuccess(context, file);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            Logger.w(TAG, "failed to check raw image exist. statusCode(" + statusCode + ") throwable(" + throwable + ")");
                            // TODO: notify download failure
                            listener.onFailure();
                        }
                    });
                } catch (Exception e) {
                    Logger.e(TAG, "failed to parse responseBody. 'byte' -> 'String'");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.w(TAG, "failed to check raw image exist. error : " + error);
                listener.onFailure();
            }
        });

        return true;
    }

    private static PersistentCookieStore getCoockieStore(final Context context, Header[] headers) {
        for (Header header : headers) {

            String name = header.getName();
            Logger.i(TAG, "header name : name(" + name + ")");
            if (TextUtils.isEmpty(name)) {
                continue;
            }

            if (name.equals("Set-Cookie")) {

                String value = header.getValue();
                value = value.replace("PHPSESSID=", "");
                value = value.replace("; path=/", "");

                Log.i(TAG, "get cookie : " + value);
                BasicClientCookie cookie = new BasicClientCookie("PHPSESSID", value);
                //cookie.setVersion(1);
                cookie.setPath("/");
                cookie.setDomain("dcimg.awalker.jp");

                PersistentCookieStore persistentCookieStore = new PersistentCookieStore(context);
                persistentCookieStore.addCookie(cookie);
                return persistentCookieStore;
            }
        }
        return null;
    }
}
