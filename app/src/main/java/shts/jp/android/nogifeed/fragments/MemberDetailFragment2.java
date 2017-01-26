package shts.jp.android.nogifeed.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.adapters.MemberFeedListAdapter;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.providers.FavoriteContentObserver;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.views.DividerItemDecoration;
import shts.jp.android.nogifeed.views.ViewMemberDetailHeader;

public class MemberDetailFragment2 extends Fragment {

    public static MemberDetailFragment2 newInstance(int memberId) {
        Bundle bundle = new Bundle();
        bundle.putInt("memberId", memberId);
        MemberDetailFragment2 memberDetailFragment2 =  new MemberDetailFragment2();
        memberDetailFragment2.setArguments(bundle);
        return memberDetailFragment2;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteContentObserver.register(getContext());
    }

    @Override
    public void onPause() {
        favoriteContentObserver.unregister(getContext());
        super.onPause();
    }

    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_member_detail2, null);

        final ViewMemberDetailHeader viewMemberDetailHeader
                = (ViewMemberDetailHeader) view.findViewById(R.id.view_member_detail_header);
        final int memberId = getArguments().getInt("memberId");
        viewMemberDetailHeader.setup(memberId);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorites.toggle(getContext(), memberId);
            }
        });

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator);

        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(getContext(), android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(getContext(), android.R.color.transparent));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        getEntries();
        return view;
    }

    private void getEntries() {
        subscriptions.add(NogiFeedApiClient
                .getMemberEntries(getArguments().getInt("memberId"), 0, 30)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entries>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Entries entries) {
                        if (entries == null) {
                            Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        final Activity activity = getActivity();
                        MemberFeedListAdapter adapter = new MemberFeedListAdapter(activity, entries);
                        adapter.setClickCallback(new MemberFeedListAdapter.OnItemClickCallback() {
                            @Override
                            public void onClick(Entry entry) {
                                activity.startActivity(BlogActivity.getStartIntent(activity, entry));
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        collapsingToolbarLayout.setTitle(entries.get(0).getMemberName());
                    }
                }));
    }

    private FavoriteContentObserver favoriteContentObserver = new FavoriteContentObserver() {
        @Override
        public void onChangeState(@State int state) {
            if (state == State.ADD) {
                Snackbar.make(coordinatorLayout, R.string.registered_favorite_member, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, R.string.unregistered_favorite_member, Snackbar.LENGTH_SHORT).show();
            }
        }
    };
}
