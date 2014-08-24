package android.shts.jp.nogifeed.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.UrlUtils;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;

/**
 * Created by saitoushouta on 2014/08/10.
 */
public class AllFeedListFragment extends android.support.v4.app.ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // この辺りを参考に実装する
        // https://github.com/rejasupotaro/Rebuild/blob/master/Rebuild/src/main/java/rejasupotaro/rebuild/fragments/EpisodeListFragment.java
    }

    private void getAllFeed() {
        AsyncRssClient.read(UrlUtils.FEED_ALL_URL, new RssClientListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, Entries entries) {
                // refresh feed list
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Show error dialog
            }
        });
    }

}
