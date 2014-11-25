package android.shts.jp.nogifeed.fragments;

import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.adapters.FavoriteFeedListAdapter;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.DataStoreUtils;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.Header;

import java.util.List;

public class FavoriteMemberFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<String> mFavoriteUrls;
    private RecyclerView mRecyclerView;
    private final Entries mEntries = new Entries();
    private int mRequestCounter = 0;
    private static final Object LOCK_OBJECT = new Object();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_feed_list, null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true); // アイテムは固定サイズ

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
        setupFavoriteMemberFeed();
    }

    private void setupFavoriteMemberFeed() {
        mFavoriteUrls = DataStoreUtils.getAllFavoriteLink(getActivity());
        for (String s : mFavoriteUrls) {
//            Log.v("setupFavoriteMemberFeed()", "setupFavoriteMemberFeed() : favorite url("
//                + s + ")");
            getAllFeed(s);
        }
    }

    private void getAllFeed(String url) {
        AsyncRssClient.read(url, new RssClientListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, Entries entries) {

                synchronized (LOCK_OBJECT) {
                    mRequestCounter++;
//                    Log.i("getAllFeed()", "getAllFeed() : mRequestCounter("
//                        + mRequestCounter + ") favorite url size("
//                        + mFavoriteUrls.size() + ")");
                    mEntries.cat(entries);
                    if (mRequestCounter >= mFavoriteUrls.size()) {
                        mRequestCounter = 0;
                        setupAdapter(mEntries.sort());
                    }
                }

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Show error toast
                Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                        Toast.LENGTH_SHORT).show();

                synchronized (LOCK_OBJECT) {
                    mRequestCounter++;
                    if (mRequestCounter >= mFavoriteUrls.size()) {
                        mRequestCounter = 0;
                        setupAdapter(mEntries.sort());
                    }
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setupAdapter(Entries entries) {
//        Log.i("setupAdapter()", "setupAdapter() : size( " + entries.size() + ") entry("
//            + entries.toString() + ")");
        mRecyclerView.setAdapter(new FavoriteFeedListAdapter(getActivity(), entries));
    }

    @Override
    public void onRefresh() {
        setupFavoriteMemberFeed();
    }

}
