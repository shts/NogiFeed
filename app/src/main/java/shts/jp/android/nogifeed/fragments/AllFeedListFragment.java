package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.Header;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.FeedListAdapter;
import shts.jp.android.nogifeed.models.Entry;

// TODO: 通信ができない場合、エラー表示を行う
// 現在は Exception が発生する
// http://www.google.com/design/spec/whats-new/whats-new.html
public class AllFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mAllFeedList;
    private FeedListAdapter mFeedListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_feed_list, null);
        mAllFeedList = (ListView) view.findViewById(R.id.all_feed_list);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.nogifeed, R.color.nogifeed, R.color.nogifeed, R.color.nogifeed);

        return view;
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
        boolean ret = shts.jp.android.nogifeed.api.AsyncRssClient.read(getActivity().getApplicationContext(),
                shts.jp.android.nogifeed.utils.UrlUtils.FEED_ALL_URL, new shts.jp.android.nogifeed.listener.RssClientListener() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, shts.jp.android.nogifeed.models.Entries entries) {
                        shts.jp.android.nogifeed.common.Logger.i("getAllFeed()", "get all member feed : size(" + entries.size() + ")");
                        // refresh feed list
                        setupAdapter(entries);
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Show error toast
                        Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                                Toast.LENGTH_SHORT).show();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

        if (!ret) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setupAdapter(shts.jp.android.nogifeed.models.Entries entries) {
        mFeedListAdapter = new shts.jp.android.nogifeed.adapters.FeedListAdapter(getActivity(), entries);
        mAllFeedList.setAdapter(mFeedListAdapter);
        mAllFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry entry = (Entry) mAllFeedList.getItemAtPosition(position);
                shts.jp.android.nogifeed.utils.IntentUtils.startBlogActivity(getActivity(), entry);
            }
        });
    }

    @Override
    public void onRefresh() {
        getAllFeed();
    }

}
