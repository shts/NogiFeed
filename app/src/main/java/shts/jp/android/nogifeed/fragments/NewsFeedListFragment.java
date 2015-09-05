package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.NewsBrowseActivity;
import shts.jp.android.nogifeed.activities.NewsListActivity;
import shts.jp.android.nogifeed.adapters.NewsListAdapter;
import shts.jp.android.nogifeed.models.News;
import shts.jp.android.nogifeed.utils.IntentUtils;
import shts.jp.android.nogifeed.utils.JsoupUtils;
import shts.jp.android.nogifeed.views.BannerShowcase;

public class NewsFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mNewsFeedList;
    private NewsListAdapter mNewsListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private BannerShowcase mShowcase;
    private NewsListActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed_list, null);
        mNewsFeedList = (ListView) view.findViewById(R.id.news_feed_list);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.nogifeed, R.color.nogifeed, R.color.nogifeed, R.color.nogifeed);

        mNewsFeedList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 1) {
                    mActivity.setActionBarDrawableAlpha(255);
                } else {
                    View header = view.getChildAt(0);
                    int height = header == null ? 0 : header.getHeight();
                    if (height <= 0) {
                        mActivity.setActionBarDrawableAlpha(0);
                    } else {
                        int alpha = Math.abs(255 * header.getTop() / height);
                        mActivity.setActionBarDrawableAlpha(alpha);
                    }
                }
            }
        });

        mShowcase = new BannerShowcase(mActivity);
        if (mNewsFeedList.getFooterViewsCount() <= 0) {
            mNewsFeedList.addHeaderView(mShowcase, null, false);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (NewsListActivity) activity;
    }

    private void getNewsFeed() {

        final boolean ret = JsoupUtils.getNewsFeed(mActivity.getApplicationContext(),
                null/* all news feed */, new JsoupUtils.GetNewsFeedListener() {
            @Override
            public void onSuccess(List<News> newsList) {
                setupAdapter(newsList);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailed() {
                // Show error toast
                Toast.makeText(mActivity, getResources().getString(R.string.feed_failure),
                        Toast.LENGTH_SHORT).show();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if (!ret) {
            // Show error toast
            Toast.makeText(mActivity, getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void getBanners() {
        mShowcase.updateBanners();
    }

    private void setupAdapter(List<News> newsList) {
        mNewsListAdapter = new NewsListAdapter(mActivity, newsList);
        mNewsFeedList.setAdapter(mNewsListAdapter);
        mNewsFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) mNewsFeedList.getItemAtPosition(position);
                startActivity(NewsBrowseActivity.createIntent(getActivity(), news));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBanners();
        getNewsFeed();
    }

    @Override
    public void onRefresh() {
        getBanners();
        getNewsFeed();
    }
}
