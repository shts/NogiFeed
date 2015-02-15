package shts.jp.android.nogifeed.listener;

import org.apache.http.Header;

/**
 * Created by saitoushouta on 2014/08/25.
 */
public interface RssClientListener {
    public void onSuccess(int statusCode, Header[] headers, shts.jp.android.nogifeed.models.Entries entries);
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);
}
