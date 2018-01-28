package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.Observable;
import rx.Subscriber;

public class FileDownloader {

    public static Observable<Uri> exec(@NonNull final Context context,
                                          @NonNull final List<String> urls,
                                          @NonNull final List<File> outputFiles) {
        return Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(final Subscriber<? super Uri> subscriber) {
                try {
                    final OkHttpClient okHttpClient = new OkHttpClient();

                    for (int i = 0; i < urls.size(); i++) {
                        final int counter = i;
                        final int limit = urls.size() - 1;

                        final Request request = new Request.Builder().url(urls.get(counter)).build();
                        final okhttp3.Response response = okHttpClient.newCall(request).execute();
                        flush(response.body().byteStream(), outputFiles.get(counter));

                        MediaScannerConnection.scanFile(
                                context.getApplicationContext(),
                                new String[]{outputFiles.get(counter).getAbsolutePath()},
                                new String[]{"image/*"},
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        if (limit <= counter) {
                                            subscriber.onNext(uri);
                                            subscriber.onCompleted();
                                        }
                                    }
                                });
                    }
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Uri> exec(@NonNull final Context context,
                                       @NonNull final String url,
                                       @NonNull final File outputFile) {
        return Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(final Subscriber<? super Uri> subscriber) {
                try {
                    final Request request = new Request.Builder().url(url).build();
                    final OkHttpClient okHttpClient = new OkHttpClient();
                    final okhttp3.Response response = okHttpClient.newCall(request).execute();
                    flush(response.body().byteStream(), outputFile);

                    MediaScannerConnection.scanFile(
                            context.getApplicationContext(),
                            new String[]{outputFile.getAbsolutePath()},
                            new String[]{"image/*"},
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    subscriber.onNext(uri);
                                    subscriber.onCompleted();
                                }
                            });
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * バイトストリームを指定したファイルオブジェクトへ書き出す
     *
     * @param inputStream 入力ストリーム
     * @param outputFile  出力先ファイルオブジェクト
     * @throws IOException
     */
    private static void flush(InputStream inputStream, File outputFile) throws IOException {
        OutputStream output = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024]; // or other buffer size
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        output.flush();
        output.close();
        inputStream.close();
    }
}
