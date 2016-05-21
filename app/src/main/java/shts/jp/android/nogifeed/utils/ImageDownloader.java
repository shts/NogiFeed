package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getSimpleName();
    private static AsyncHttpClient client = new AsyncHttpClient();

    private final List<String> urls;
    private final Context context;

    private List<FileAsyncHttpResponseHandler> handlers = new ArrayList<>();
    private List<Response> responseList = new ArrayList<>();
    private int counter = 0;

    public ImageDownloader(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
        for (String url : urls) {
            handlers.add(createResponseHandler(url));
        }
    }

    private FileAsyncHttpResponseHandler createResponseHandler(final String url) {
        final File file = getFileFromUrl(url);
        return new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                final Response res = new Response(
                        Response.Result.FAILED, url, statusCode, headers, throwable, file);
                responseList.add(res);
                onResponse(res);
                addCounter();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                final Response res = new Response(
                        Response.Result.SUCCESS, url, statusCode, headers, null, file);
                responseList.add(res);
                onResponse(res);
                addCounter();
            }
        };
    }

    public final boolean get() {
        if (!NetworkUtils.enableNetwork(context)) {
            Log.w(TAG, "cannot start download because of enable network");
            return false;
        }
        onStart();
        final int N = urls.size();
        for (int i = 0; i < N; i++) {
            client.get(urls.get(i), handlers.get(i));
        }
        return true;
    }

    private void addCounter() {
        counter++;
        if (urls.size() <= counter) {
            onComplete(responseList);
        }
    }

    public static final class Response {
        enum Result { SUCCESS, FAILED}
        final Result result;
        final String url;
        final int statusCode;
        final Header[] headers;
        final Throwable throwable;
        final File file;
        Response(Result result, String url, int statusCode,
                 Header[] headers, Throwable throwable, File file) {
            this.result = result;
            this.url = url;
            this.statusCode = statusCode;
            this.headers = headers;
            this.throwable = throwable;
            this.file = file;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("result(").append(result.name()).append(") ");
            sb.append("url(").append(url).append(") ");
            sb.append("statusCode(").append(statusCode).append(") ");
            sb.append("headers(").append((headers == null?"null":headers)).append(") ");
            sb.append("throwable(").append(throwable).append(") ");
            sb.append("file(").append(file).append(") ");
            return sb.toString();
        }
        public File getFile() {
            return file;
        }
    }

    public File getFileFromUrl(String url) {
        // デフォルトは標準のダウンロードフォルダを指定
        return new File(SdCardUtils.getDownloadFilePath(url));
    }

    public void onStart() {}

    public void onResponse(Response response) {}

    public void onComplete(List<Response> responseList) {}

}