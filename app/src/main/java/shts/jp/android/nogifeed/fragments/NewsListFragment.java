package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.NewsBrowseActivity;
import shts.jp.android.nogifeed.adapters.NewsListAdapter;
import shts.jp.android.nogifeed.api.AsyncNewsClient;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.views.dialogs.NewsTypeFilterDialog;

public class NewsListFragment extends Fragment {

    private static final String TAG = NewsListFragment.class.getSimpleName();

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

    private ListView listView;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed_list, null);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsTypeFilterDialog dialog = new NewsTypeFilterDialog();
                dialog.setCallbacks(new NewsTypeFilterDialog.Callbacks() {
                    @Override
                    public void onClickPositiveButton() {
                    }
                    @Override
                    public void onClickNegativeButton() {}
                });
                dialog.show(getFragmentManager(), NewsTypeFilterDialog.class.getSimpleName());
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllNews();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary, R.color.primary, R.color.primary, R.color.primary);

        listView = (ListView) view.findViewById(R.id.news_feed_list);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator);

        getAllNews();
        return view;
    }

    private void getAllNews() {
        if (!AsyncNewsClient.get(getActivity())) {
            Snackbar.make(coordinatorLayout, R.string.failed_to_get_news, Snackbar.LENGTH_LONG)
                    .show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe
    public void onGotNewsFeedList(AsyncNewsClient.GetNewsFeedCallback callback) {
        swipeRefreshLayout.setRefreshing(false);
        if (callback == null || callback.hasError()) {
            Snackbar.make(coordinatorLayout, R.string.failed_to_get_news, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) listView.getItemAtPosition(position);
                startActivity(NewsBrowseActivity.getStartIntent(getActivity(), news));
            }
        });
        listView.setAdapter(new NewsListAdapter(getActivity(), callback.newsList));
    }
}
