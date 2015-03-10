package shts.jp.android.nogifeed.listener;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.utils.AtomRssParser;

public abstract class RssClientFinishListener extends AsyncHttpResponseHandler {

    private static final String TAG = RssClientFinishListener.class.getSimpleName();

    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);
    private final android.os.Handler HANDLER = new android.os.Handler(Looper.getMainLooper());

    private int mCounterSize;
    private int mCounter = 0;
    private final Entries mEntries = new Entries();

    public void setCounterSize(int size) {
        mCounterSize = size;
    }

    @Override
    public void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBody) {
        Logger.d(TAG, "onSuccess()");
        EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                final InputStream is = new ByteArrayInputStream(responseBody);
                final Entries entries = AtomRssParser.parse(is);
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccessWrapper(statusCode, headers, entries);
                        addCounter(entries);
                    }
                });
            }
        });
    }

    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Logger.w(TAG, "onFailure()");
        onFailureWrapper(statusCode, headers, responseBody, error);
        addCounter(null);
    }

    private synchronized void addCounter(Entries entries) {
        Logger.d(TAG, "addCounter() : size(" + entries == null ? "null" : entries.size() + ")");
        mCounter++;
        if (entries != null) {
            mEntries.addAll(entries);
        }
        if (mCounterSize <= mCounter) {
            Logger.d(TAG, "finished async task");
            onFinish(mEntries);
        }
    }

    /**
     * Wrapped {@link AsyncHttpResponseHandler#onSuccess(int, Header[], byte[])}. called on UI thread.
     * @param statusCode statusCode
     * @param headers response headers
     * @param entries entries got
     */
    public abstract void onSuccessWrapper(final int statusCode, final Header[] headers, final Entries entries);
    /**
     * Wrapped {@link AsyncHttpResponseHandler#onFailure(int, Header[], byte[], Throwable)}. called on UI thread.
     * @param statusCode statusCode
     * @param headers response headers
     * @param responseBody response body
     * @param error error detail
     */
    public abstract void onFailureWrapper(int statusCode, Header[] headers, byte[] responseBody, Throwable error);
    /**
     * called when got all feed. called on UI thread.
     * @param entries entries got. return null if request failed.
     */
    public abstract void onFinish(final Entries entries);
}