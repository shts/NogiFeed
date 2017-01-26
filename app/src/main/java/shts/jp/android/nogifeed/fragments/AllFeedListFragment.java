package shts.jp.android.nogifeed.fragments;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.adapters.AllFeedListAdapter;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.providers.FavoriteContentObserver;
import shts.jp.android.nogifeed.views.HackySwipeRefreshLayout;

public class AllFeedListFragment extends Fragment {

    private static final int PAGE_LIMIT = 30;
    private int counter = 0;

    private ListView listView;
    private AllFeedListAdapter adapter;
    private LinearLayout footerView;
    private HackySwipeRefreshLayout swipeRefreshLayout;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private final AllFeedListAdapter.OnPageMaxScrolledListener scrolledListener
            = new AllFeedListAdapter.OnPageMaxScrolledListener() {
        @Override
        public void onScrolledMaxPage() {
            getNextFeed();
        }
    };

    private final FavoriteContentObserver favoriteContentObserver
            = new FavoriteContentObserver() {
        @Override
        public void onChangeState(@State int state) {
            adapter.notifyDataSetChanged();
        }
    };

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_feed_list, null);
        listView = (ListView) view.findViewById(R.id.all_feed_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getItemAtPosition(position);
                if (entry != null) {
                    getActivity().startActivity(
                            BlogActivity.getStartIntent(getActivity(), entry));
                }
            }
        });

        // SwipeRefreshLayoutの設定
        swipeRefreshLayout = (HackySwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllFeeds();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
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
        swipeRefreshLayout.setRefreshing(true);

        subscriptions.add(NogiFeedApiClient.getAllEntries((counter * PAGE_LIMIT), PAGE_LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                })
                .subscribe(new Subscriber<Entries>() {
                    @Override
                    public void onCompleted() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Entries entries) {
                        if (entries != null) {
                            adapter = new AllFeedListAdapter(getActivity(), entries);
                            adapter.setPageMaxScrolledListener(scrolledListener);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getActivity(), R.string.feed_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    private void getNextFeed() {
        swipeRefreshLayout.setRefreshing(false);

        if (footerView != null) {
            footerView.setVisibility(View.VISIBLE);
        }

        counter++;
        subscriptions.add(NogiFeedApiClient.getAllEntries((counter * PAGE_LIMIT), PAGE_LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entries>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        footerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Entries entries) {
                        footerView.setVisibility(View.VISIBLE);
                        if (entries != null) {
                            adapter.add(entries);
                        } else {
                            Toast.makeText(getActivity(), R.string.feed_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    // 推しメン登録は個人ページより行われるので resume <-> pause だと拾えない
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteContentObserver.register(getContext());
    }

    @Override
    public void onDestroyView() {
        favoriteContentObserver.unregister(getContext());
        subscriptions.unsubscribe();
        super.onDestroyView();
    }

}
