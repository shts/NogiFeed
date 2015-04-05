package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
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

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.FavoriteFeedListAdapter;
import shts.jp.android.nogifeed.api.AsyncRssClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientFinishListener;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.views.MultiSwipeRefreshLayout;

// TODO: インストール後に何度か起動された時、アプリ評価を誘導する View を表示する
// TODO: View にお気に入り機能と共有機能を追加する
public class FavoriteMemberFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FavoriteMemberFeedListFragment.class.getSimpleName();

    private MultiSwipeRefreshLayout mMultiSwipeRefreshLayout;
    private List<String> mFavoriteUrls;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private final Entries mEntries = new Entries();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_feed_list, null);

        mEmptyView = view.findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true); // アイテムは固定サイズ

        // SwipeRefreshLayoutの設定
        mMultiSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.refresh);
        mMultiSwipeRefreshLayout.setOnRefreshListener(this);
        mMultiSwipeRefreshLayout.setSwipeableChildren(R.id.refresh, R.id.empty_view);
        mMultiSwipeRefreshLayout.setColorSchemeResources(
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
        if (mFavoriteUrls == null || mFavoriteUrls.isEmpty()) {
            if (mMultiSwipeRefreshLayout.isRefreshing()) {
                mMultiSwipeRefreshLayout.setRefreshing(false);
            }
            Logger.d(TAG, "setupFavoriteMemberFeed() : no favorite feed.");
            setVisibilityEmptyView(true);
            Toast.makeText(getActivity(), R.string.empty_favorite, Toast.LENGTH_LONG).show();
            return;
        }

        getAllFeeds(mFavoriteUrls);
    }

    private void getAllFeeds(List<String> favoriteUrls) {
        // clear feed list before add new feed.
        mEntries.clear();

        boolean ret = AsyncRssClient.read(getActivity().getApplicationContext(), favoriteUrls, new RssClientFinishListener() {
            @Override
            public void onSuccessWrapper(int statusCode, Header[] headers, Entries entries) {
            }

            @Override
            public void onFailureWrapper(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

            @Override
            public void onFinish(Entries entries) {
                if (entries == null || entries.isEmpty()) {
                    // Show error toast
                    Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                            Toast.LENGTH_SHORT).show();
                    setVisibilityEmptyView(true);
                } else {
                    setVisibilityEmptyView(false);
                    setupAdapter(entries.sort());
                }

                if (mMultiSwipeRefreshLayout.isRefreshing()) {
                    mMultiSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if (!ret) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            if (mMultiSwipeRefreshLayout.isRefreshing()) {
                mMultiSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setupAdapter(Entries entries) {
        Logger.v("setupAdapter()", "setupAdapter() : size( " + entries.size() + ") entry(" + entries.toString() + ")");
        setVisibilityEmptyView(false);
        mRecyclerView.setAdapter(new FavoriteFeedListAdapter(getActivity(), entries));
    }

    @Override
    public void onRefresh() {
        setupFavoriteMemberFeed();
    }

    private void setVisibilityEmptyView(boolean isVisible) {
        if (isVisible) {
            // mRecyclerView を setVisiblity(View.GONE) で表示にするとプログレスが表示されない
            mEntries.clear();
            mRecyclerView.setAdapter(null);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

}
