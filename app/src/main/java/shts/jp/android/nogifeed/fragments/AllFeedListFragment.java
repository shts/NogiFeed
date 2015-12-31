package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.adapters.AllFeedListAdapter;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;

public class AllFeedListFragment extends Fragment {

    private static final String TAG = AllFeedListFragment.class.getSimpleName();

    private static final int PAGE_LIMIT = 30;
    private int counter = 0;

    private ListView listView;
    private AllFeedListAdapter adapter;
    private LinearLayout footerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static List<Entry> cache;

    private final AllFeedListAdapter.OnPageMaxScrolledListener scrolledListener
            = new AllFeedListAdapter.OnPageMaxScrolledListener() {
        @Override
        public void onScrolledMaxPage() {
            getNextFeed();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_feed_list, null);
        listView = (ListView) view.findViewById(R.id.all_feed_list);

        // SwipeRefreshLayoutの設定
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllFeeds();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.nogifeed, R.color.nogifeed, R.color.nogifeed, R.color.nogifeed);

        footerView = (LinearLayout) inflater.inflate(R.layout.list_item_more_load, null);
        footerView.setVisibility(View.GONE);

        listView.addFooterView(footerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAllFeeds();
    }

    private void getAllFeeds() {
        Entry.all(PAGE_LIMIT, counter);
    }

    private void getNextFeed() {
        if (footerView != null) {
            footerView.setVisibility(View.VISIBLE);
        }
        counter++;
        Entry.next(PAGE_LIMIT, (counter * PAGE_LIMIT));
    }

    @Subscribe
    public void onGotNextEntries(Entry.GotAllEntryCallback.Next callback) {
        if (footerView != null) {
            footerView.setVisibility(View.GONE);
        }
        if (callback.e == null) {
            adapter.add(callback.entries);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onGotAllEntries(Entry.GotAllEntryCallback.All callback) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (callback.e == null) {
            adapter = new AllFeedListAdapter(getActivity(), callback.entries);
            adapter.setPageMaxScrolledListener(scrolledListener);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Entry entry = (Entry) parent.getItemAtPosition(position);
                    if (entry != null) {
                        getActivity().startActivity(
                                BlogActivity.getStartIntent(getActivity(), entry.getObjectId()));
                    }
                }
            });
        }
    }

    // 推しメン登録は個人ページより行われるので resume <-> pause だと拾えない
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusHolder.get().register(this);
    }

    @Override
    public void onDestroy() {
        BusHolder.get().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onChangedFavorite(Favorite.ChangedFavoriteState callback) {
        adapter.notifyDataSetChanged();
    }
}
