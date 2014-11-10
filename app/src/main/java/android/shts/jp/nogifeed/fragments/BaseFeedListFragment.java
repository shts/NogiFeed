package android.shts.jp.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.activities.FeedListActivity;
import android.shts.jp.nogifeed.adapters.FeedListAdapter;
import android.shts.jp.nogifeed.models.Entries;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.Header;

import java.util.List;

public class BaseFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: 抽象化したいけどうまく設計できない
    private ListView mFeedList;
    private FeedListAdapter mFeedListAdapter;
    private FeedListActivity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FeedListActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_feed_list, null);
        mFeedList = (ListView) view.findViewById(R.id.feed_list);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.nogifeed, R.color.nogifeed, R.color.nogifeed, R.color.nogifeed);

        return view;
    }

    @Override
    public void onRefresh() {

    }

    protected void getFeed(String url) {

    }

    protected void getFeed(List<String> urls) {

    }

}
