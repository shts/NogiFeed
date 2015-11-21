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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.FavoriteFeedListAdapter;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.views.MultiSwipeRefreshLayout;

// TODO: インストール後に何度か起動された時、アプリ評価を誘導する View を表示する
// TODO: View にお気に入り機能と共有機能を追加する
public class FavoriteMemberFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FavoriteMemberFeedListFragment.class.getSimpleName();

    private MultiSwipeRefreshLayout mMultiSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

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
        mMultiSwipeRefreshLayout.setSwipeableChildren(R.id.recyclerview, R.id.empty_view);
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
        Favorite.getQuery().findInBackground(new FindCallback<Favorite>() {
            @Override
            public void done(List<Favorite> objects, ParseException e) {
                if (e != null || objects == null || objects.isEmpty()) {
                    if (mMultiSwipeRefreshLayout.isRefreshing()) {
                        mMultiSwipeRefreshLayout.setRefreshing(false);
                    }
                    Logger.d(TAG, "setupFavoriteMemberFeed() : no favorite feed.");
                    setVisibilityEmptyView(true);
                    Toast.makeText(getActivity(), R.string.empty_favorite, Toast.LENGTH_LONG).show();
                    return;
                }
                List<String> memberObjectIdList = new ArrayList<>();
                for (Favorite favorite : objects) {
                    memberObjectIdList.add(favorite.getMemberObjectId());
                }
                getAllFeeds(memberObjectIdList);
            }
        });
    }

    private void getAllFeeds(List<String> memberObjectIdList) {
        ParseQuery<Entry> query = Entry.getQuery();
        query.whereContainedIn("author_id", memberObjectIdList);
        query.findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                if (e != null || entries == null || entries.isEmpty()) {
                    Logger.e(TAG, "cannot get all feed entries", e);
                    // Show error toast
                    Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                            Toast.LENGTH_SHORT).show();
                    setVisibilityEmptyView(true);
                    if (mMultiSwipeRefreshLayout != null) {
                        if (mMultiSwipeRefreshLayout.isRefreshing()) {
                            mMultiSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    return;
                }
                setVisibilityEmptyView(false);
                setupAdapter(entries);
                if (mMultiSwipeRefreshLayout != null) {
                    if (mMultiSwipeRefreshLayout.isRefreshing()) {
                        mMultiSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    private void setupAdapter(List<Entry> entries) {
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
            //mEntries.clear();
            mRecyclerView.setAdapter(null);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

}
