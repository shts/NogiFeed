package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
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
import shts.jp.android.nogifeed.api.AsyncRssClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientFinishListener;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.utils.IntentUtils;
import shts.jp.android.nogifeed.utils.UrlUtils;

// TODO: 通信ができない場合、エラー表示を行う
// 現在は Exception が発生する
// http://www.google.com/design/spec/whats-new/whats-new.html
public class AllFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = AllFeedListFragment.class.getSimpleName();

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
        getAllFeeds();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getAllFeeds() {

        boolean ret = AsyncRssClient.read(getActivity().getApplicationContext(),
                UrlUtils.FEED_ALL_URL, new RssClientFinishListener() {
            @Override
            public void onSuccessWrapper(int statusCode, Header[] headers, Entries entries) {
                // do nothing. set up feed at onFinish()
            }

            @Override
            public void onFailureWrapper(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // do nothing. set up feed at onFinish()
            }

            @Override
            public void onFinish(Entries entries) {
                Logger.v("getAllFeed()", "get all member feed : size(" +
                        (entries == null ? "null" : entries.size()) + ")");

                // check Activity life cycle
                final Activity activity = getActivity();
                if (activity == null) {
                    Logger.w(TAG, "activity already finished");
                    return;
                }
                if (entries == null || entries.isEmpty()) {
                    // Show error toast
                    Toast.makeText(activity, R.string.feed_failure,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // refresh feed list
                    setupAdapter(entries);
                }

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

    private void setupAdapter(Entries entries) {
        mFeedListAdapter = new FeedListAdapter(getActivity(), entries);
        mAllFeedList.setAdapter(mFeedListAdapter);
        mAllFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry entry = (Entry) mAllFeedList.getItemAtPosition(position);
                IntentUtils.startBlogActivity(getActivity(), entry);
            }
        });
    }

    @Override
    public void onRefresh() {
        getAllFeeds();
    }

}
