package shts.jp.android.nogifeed.utils;


import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.List;

import shts.jp.android.nogifeed.models.eventbus.BusHolder;

/**
 * 設定された秒数までコールバックを待ち合わせる画像ダウンローダー
 * ダウンロードが早く終了しチラついて見える現象を回避するため
 */
public class WaitMinimunImageDownloader extends ImageDownloader {

    private static final String TAG = WaitMinimunImageDownloader.class.getSimpleName();

    public WaitMinimunImageDownloader(Context context, List<String> urls) { super(context, urls);}

    private boolean isTimeUp = false;
    private boolean isFinishDownload = false;
    private List<Response> responseList;
    private final Object LOCK = new Object();

    /**
     * ダウンロード完了のコールバック
     */
    public static class Callback {
        /** 1ファイルのダウンロード終了ごとにコールされる */
        public static class ResponseDownloadImage {
            public final File file;
            ResponseDownloadImage(File file) { this.file = file; }
        }
        /** すべてのファイルのダウンロードが終了したらコールされる */
        public static class CompleteDownloadImage {
            public List<Response> responseList;
            CompleteDownloadImage(List<Response> responseList) { this.responseList = responseList; }
        }
    }


    @Override
    final public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        isTimeUp = false;
        isFinishDownload = false;
        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "time up");
                synchronized (LOCK) {
                    isTimeUp = true;
                    if (isFinishDownload) {
                        BusHolder.get().post(new Callback.CompleteDownloadImage(responseList));
                    }
                }
            }
        }, getWaitTime());
    }

    @Override
    final public void onResponse(Response response) {
        if (response.result != Response.Result.SUCCESS) {
            Log.e(TAG, "failed to download image : response("
                    + response.toString() + ")");
        }
        BusHolder.get().post(new Callback.ResponseDownloadImage(response.file));
    }

    @Override
    final public void onComplete(List<Response> responseList) {
        Log.i(TAG, "onComplete");
        this.responseList = responseList;
        synchronized (LOCK) {
            isFinishDownload = true;
            if (isTimeUp) {
                BusHolder.get().post(new Callback.CompleteDownloadImage(responseList));
            }
        }
    }

    /**
     * 待ち合わせ時間(ms)
     * @return デフォルトは1秒
     */
    public int getWaitTime() {
        return 1500;
    }

}