package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.AllMemberActivity;
import shts.jp.android.nogifeed.adapters.FavoriteFeedListAdapter;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;

// TODO: インストール後に何度か起動された時、アプリ評価を誘導する View を表示する
// TODO: View にお気に入り機能と共有機能を追加する
public class FavoriteMemberFeedListFragment extends Fragment {

    private static final String TAG = FavoriteMemberFeedListFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_feed_list, null);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AllMemberActivity.getStartIntent(getActivity());
                startActivityForResult(intent, 0);
            }
        });

        emptyView = view.findViewById(R.id.empty_view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true); // アイテムは固定サイズ

        // SwipeRefreshLayoutの設定
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupFavoriteMemberFeed();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        setupFavoriteMemberFeed();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            swipeRefreshLayout.post(new Runnable() {
                @Override public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    setupFavoriteMemberFeed();
                }
            });
        }
    }

    private void setupFavoriteMemberFeed() {
        setVisibilityEmptyView(false);
        Favorite.all();
    }

    @Subscribe
    public void onGotAllFavorities(Favorite.GetFavoritesCallback callback) {
        if (callback.hasError()) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            setVisibilityEmptyView(true);
            return;
        }
        Entry.findById(30, 0, callback.favorites);
    }

    @Subscribe
    public void onGotAllEntries(Entry.GotAllEntryCallback.FindById callback) {
        setVisibilityEmptyView(true);
        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        if (callback.hasError()) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        setVisibilityEmptyView(false);
        recyclerView.setAdapter(new FavoriteFeedListAdapter(getActivity(), callback.entries));
    }

    private void setVisibilityEmptyView(boolean isVisible) {
        if (isVisible) {
            // recyclerView を setVisiblity(View.GONE) で表示にするとプログレスが表示されない
            recyclerView.setAdapter(null);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

}
