package android.shts.jp.nogifeed.listener;

import android.shts.jp.nogifeed.models.Entries;

import org.apache.http.Header;

/**
 * Created by saitoushouta on 2014/08/25.
 */
public interface RssClientListener {
    public void onSuccess(int statusCode, Header[] headers, Entries entries);
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);
}
