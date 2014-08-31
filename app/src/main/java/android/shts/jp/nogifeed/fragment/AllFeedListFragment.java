package android.shts.jp.nogifeed.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.adapters.AllFeedListAdapter;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;

public class AllFeedListFragment extends android.support.v4.app.Fragment {

    private ListView mAllFeedList;
    private AllFeedListAdapter mAllFeedListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_feed_list, null);
        mAllFeedList = (ListView) view.findViewById(R.id.all_feed_list);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAllFeed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getAllFeed() {
        AsyncRssClient.read(UrlUtils.FEED_ALL_URL, new RssClientListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, Entries entries) {
                // refresh feed list
                setupAdapterOnUiThread(entries);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Show error dialog
                Toast.makeText(getActivity(), "フィードの取得に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapterOnUiThread(final Entries entries) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupAdapter(entries);
            }
        });
    }

    private void setupAdapter(Entries entries) {
        mAllFeedListAdapter = new AllFeedListAdapter(getActivity(), entries);
        mAllFeedList.setAdapter(mAllFeedListAdapter);
    }

}
