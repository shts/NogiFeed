package android.shts.jp.nogifeed.fragments;

import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.adapters.CardListAdapter;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.DataStoreUtils;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class FavoriteMemberFeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<String> mFavoriteUrls;
    private RecyclerView mRecyclerView;

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

//        List<CardData> list = new ArrayList<CardData>();
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        mRecyclerView.setAdapter(new CardListAdapter(getActivity(), list));
//
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

//        mFavoriteUrls = DataStoreUtils.getAllFavoriteLink(getActivity());
//        for (String s : mFavoriteUrls) {
//            getAllFeed(s);
//        }
        getAllFeed(UrlUtils.FEED_ALL_URL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getAllFeed(String url) {
        AsyncRssClient.read(url, new RssClientListener() {
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
//        mFeedListAdapter = new FeedListAdapter(getActivity(), entries);
//        mAllFeedList.setAdapter(mFeedListAdapter);
//        mAllFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Entry entry = (Entry) mAllFeedList.getItemAtPosition(position);
//                IntentUtils.startBlogActivity(mActivity, entry);
//            }
//        });
//        List<CardData> list = new ArrayList<CardData>();
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
//        list.add(new CardData("Title", "Summary"));
        mRecyclerView.setAdapter(new CardListAdapter(getActivity(), entries));

    }

    @Override
    public void onRefresh() {
        mFavoriteUrls = DataStoreUtils.getAllFavoriteLink(getActivity());
        for (String s : mFavoriteUrls) {
            getAllFeed(s);
        }
    }

}
