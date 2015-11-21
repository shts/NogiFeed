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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.squareup.otto.Subscribe;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.adapters.AllFeedListAdapter;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.models.eventbus.EventOnChangeFavoriteState;

public class AllFeedListFragment extends Fragment {

    private static final String TAG = AllFeedListFragment.class.getSimpleName();

    private static final int PAGE_LIMIT = 30;
    private int counter = 0;

    private ListView listView;
    private AllFeedListAdapter adapter;
    private LinearLayout footerView;

    private final AllFeedListAdapter.OnPageMaxScrolledListener scrolledListener
            = new AllFeedListAdapter.OnPageMaxScrolledListener() {
        @Override
        public void onScrolledMaxPage() {
            getNextFeed();
        }
    };

    private abstract class Callback implements FindCallback<Entry> {
        @Override
        public void done(List<Entry> entries, ParseException e) {
            if (e != null || entries == null || entries.isEmpty()) {
                // TODO: show error toast
                Logger.e(TAG, "cannot get entries", e);
            } else {
                done(entries);
            }
            if (footerView != null) {
                footerView.setVisibility(View.GONE);
            }
        }
        public abstract void done(List<Entry> entries);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_feed_list, null);
        listView = (ListView) view.findViewById(R.id.all_feed_list);

        // SwipeRefreshLayoutの設定
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
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
        Entry.getQuery(PAGE_LIMIT, counter).findInBackground(new Callback() {
            @Override
            public void done(final List<Entry> entries) {
                for (Entry e : entries) {
                    e.encache();
                }
                adapter = new AllFeedListAdapter(getActivity(), entries);
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
        });
    }

    private void getNextFeed() {
        if (footerView != null) {
            footerView.setVisibility(View.VISIBLE);
        }
        counter++;
        Entry.getQuery(PAGE_LIMIT, (counter * PAGE_LIMIT)).findInBackground(new Callback() {
            @Override
            public void done(List<Entry> entries) {
                for (Entry e : entries) {
                    e.encache();
                }
                adapter.add(entries);
                adapter.notifyDataSetChanged();
            }
        });
    }

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
    public void onChange(EventOnChangeFavoriteState eventOnChangeFavoriteState) {
        Logger.v(TAG, "onChange favorite");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
