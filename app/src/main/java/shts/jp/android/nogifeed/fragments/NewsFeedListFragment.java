package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.NewsListAdapter;
import shts.jp.android.nogifeed.models.News;
import shts.jp.android.nogifeed.utils.IntentUtils;
import shts.jp.android.nogifeed.utils.JsoupUtils;

public class NewsFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mNewsFeedList;
    private NewsListAdapter mNewsListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        return view;
    }

    private void getNewsFeed() {

        final boolean ret = JsoupUtils.getNewsFeed(getActivity().getApplicationContext(),
                new JsoupUtils.GetNewsFeedListener() {
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
                Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                        Toast.LENGTH_SHORT).show();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if (!ret) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setupAdapter(List<News> newsList) {
        mNewsListAdapter = new NewsListAdapter(getActivity(), newsList);
        mNewsFeedList.setAdapter(mNewsListAdapter);
        mNewsFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) mNewsFeedList.getItemAtPosition(position);
                IntentUtils.startNewsActivity(getActivity(), news);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getNewsFeed();
    }

    @Override
    public void onRefresh() {
        getNewsFeed();
    }
}
