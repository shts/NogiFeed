package shts.jp.android.nogifeed.fragments;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.AllMemberActivity;
import shts.jp.android.nogifeed.adapters.FavoriteFeedListAdapter;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.providers.dao.Favorite;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.views.HackySwipeRefreshLayout;

public class FavoriteMemberFeedListFragment extends Fragment {

    private HackySwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
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
        swipeRefreshLayout = (HackySwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEntries();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        getEntries();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            getEntries();
        }
    }

    @Override
    public void onDestroyView() {
        subscriptions.unsubscribe();
        super.onDestroyView();
    }

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private void getEntries() {
        setVisibilityEmptyView(false);

        subscriptions.add(createFavoriteMemberObservable()
                .map(new Func1<Favorites, List<Integer>>() {
                    @Override
                    public List<Integer> call(Favorites favorites) {
                        List<Integer> memberIds = new ArrayList<>();
                        for (Favorite f : favorites) {
                            memberIds.add(f.memberId);
                        }
                        return memberIds;
                    }
                })
                .flatMap(new Func1<List<Integer>, Observable<Entries>>() {
                    @Override
                    public Observable<Entries> call(List<Integer> integers) {
                        return NogiFeedApiClient.getMemberEntries(integers, 0, 30);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entries>() {
                    @Override
                    public void onCompleted() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        setVisibilityEmptyView(false);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Entries entries) {
                        if (entries == null || entries.isEmpty()) {
                            setVisibilityEmptyView(true);
                            return;
                        }
                        recyclerView.setAdapter(new FavoriteFeedListAdapter(getActivity(), entries));
                    }
                }));
    }

    private Observable<Favorites> createFavoriteMemberObservable() {
        return Observable.create(new Observable.OnSubscribe<Favorites>() {
            @Override
            public void call(Subscriber<? super Favorites> subscriber) {
                try {
                    subscriber.onNext(Favorites.all(getContext()));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
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
