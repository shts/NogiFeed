package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;

import java.util.ArrayList;

import shts.jp.android.nogifeed.BuildConfig;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.adapters.BlogFeedListAdapter;
import shts.jp.android.nogifeed.adapters.FeedListAdapter;
import shts.jp.android.nogifeed.api.AsyncBlogFeedClient;
import shts.jp.android.nogifeed.api.AsyncRssClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientFinishListener;
import shts.jp.android.nogifeed.models.BlogEntry;
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
    private BlogFeedListAdapter mBlogFeedListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mFooterView;
    private ProgressBar mProgress;

    private AsyncBlogFeedClient.Target mTarget = new AsyncBlogFeedClient.Target();

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

        mFooterView = (LinearLayout) inflater.inflate(R.layout.list_item_more_load, null);
        mProgress = (ProgressBar) mFooterView.findViewById(R.id.more_progress);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAllFeeds(null);
    }

    private void getAllFeeds(AsyncBlogFeedClient.Target target) {
        if (target == null) {
            mTarget = new AsyncBlogFeedClient.Target();
            target = mTarget;
        }

        boolean ret = AsyncBlogFeedClient.getBlogEntry(getActivity().getApplicationContext(),
                target, new AsyncBlogFeedClient.Callbacks() {
                    @Override
                    public void onFinish(ArrayList<BlogEntry> blogEntries) {
                        Logger.v("getAllFeed()", "get all member feed : size(" +
                                (blogEntries == null ? "null" : blogEntries.size()) + ")");

                        if (BuildConfig.DEBUG) {
                            if (blogEntries != null) {
                                for (BlogEntry blogEntry : blogEntries) {
                                    Log.v(TAG, "blogEntry {" + blogEntry.toString() + "}");
                                }
                            }
                        }

                        // check Activity life cycle
                        final Activity activity = getActivity();
                        if (activity == null) {
                            Logger.w(TAG, "activity already finished");
                            return;
                        }

                        if (blogEntries == null || blogEntries.isEmpty()) {
                            Logger.w(TAG, "failed to get blog entries");
                            Toast.makeText(activity, R.string.feed_failure,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // refresh feed list
                        setupAdapter(blogEntries);

                        if (mSwipeRefreshLayout != null) {
                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });

        if (!ret) {
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setupAdapter(ArrayList<BlogEntry> blogEntries) {
        final int footerCount = mAllFeedList.getFooterViewsCount();
        if (footerCount > 0) {
            mAllFeedList.removeFooterView(mFooterView);
        }
        mAllFeedList.setFooterDividersEnabled(false);

        if (mBlogFeedListAdapter != null) {
            for (BlogEntry e : blogEntries) {
                mBlogFeedListAdapter.add(e);
            }
            mBlogFeedListAdapter.notifyDataSetChanged();
            if (mAllFeedList.getAdapter() == null) {
                mAllFeedList.setAdapter(mBlogFeedListAdapter);
                mAllFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        BlogEntry blogEntry = (BlogEntry) mAllFeedList.getItemAtPosition(position);
                        startActivity(BlogActivity.getStartIntent(getActivity(), blogEntry));
                    }
                });
            }
            return;
        }
        mBlogFeedListAdapter = new BlogFeedListAdapter(getActivity(), blogEntries);
        mBlogFeedListAdapter.setOnPageMaxScrolled(
                new BlogFeedListAdapter.OnPageMaxScrolledListener() {
                    @Override
                    public void onScrolledMaxPage() {
                        mAllFeedList.addFooterView(mFooterView, null, true);
                        getAllFeeds(mTarget);
                    }
                });
        mAllFeedList.setAdapter(mBlogFeedListAdapter);
        mAllFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BlogEntry blogEntry = (BlogEntry) mAllFeedList.getItemAtPosition(position);
                startActivity(BlogActivity.getStartIntent(getActivity(), blogEntry));
            }
        });
    }

    @Override
    public void onRefresh() {
        mBlogFeedListAdapter = null;
        getAllFeeds(null);
    }

}
