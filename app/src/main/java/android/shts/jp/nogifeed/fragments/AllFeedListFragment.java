package android.shts.jp.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.shts.jp.nogifeed.MemberCreator;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.activities.MainActivity;
import android.shts.jp.nogifeed.adapters.FeedListAdapter;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.Header;

public class AllFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mAllFeedList;
    private FeedListAdapter mFeedListAdapter;
    private MainActivity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MemberCreator mMemberCreator;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
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
                setupAdapter(entries);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Show error dialog
                Toast.makeText(getActivity(), "フィードの取得に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(Entries entries) {
        mFeedListAdapter = new FeedListAdapter(getActivity(), entries);
        mAllFeedList.setAdapter(mFeedListAdapter);
        mAllFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry entry = (Entry) mAllFeedList.getItemAtPosition(position);
                mActivity.changeFragment(createBlogFragment(entry));
            }
        });
    }

    private BlogFragment createBlogFragment(Entry entry) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Entry.KEY, entry);

        BlogFragment blogFragment = new BlogFragment();
        blogFragment.setArguments(bundle);

        return blogFragment;
    }

    @Override
    public void onRefresh() {
        getAllFeed();
    }

}
