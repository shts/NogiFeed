package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.adapters.MemberFeedListAdapter;
import shts.jp.android.nogifeed.entities.Blog;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.views.DividerItemDecoration;
import shts.jp.android.nogifeed.views.ViewMemberDetailHeader;

public class MemberDetailFragment2 extends Fragment {

    private static final String TAG = MemberDetailFragment2.class.getSimpleName();

    public static MemberDetailFragment2 newInstance(String memberObjectId) {
        Bundle bundle = new Bundle();
        bundle.putString("memberObjectId", memberObjectId);
        MemberDetailFragment2 memberDetailFragment2 =  new MemberDetailFragment2();
        memberDetailFragment2.setArguments(bundle);
        return memberDetailFragment2;
    }

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

    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_detail2, null);

        final ViewMemberDetailHeader viewMemberDetailHeader
                = (ViewMemberDetailHeader) view.findViewById(R.id.view_member_detail_header);
        final String memberObjectId = getArguments().getString("memberObjectId");
        viewMemberDetailHeader.setup(memberObjectId);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite.toggle(memberObjectId);
            }
        });

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator);

        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        Entry.findById(30, 0, memberObjectId);
        return view;
    }

    @Subscribe
    public void onGotAllEntries(Entry.GotAllEntryCallback.FindById callback) {
        if (callback.hasError()) {
            Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show();
            return;
        }
        final Activity activity = getActivity();
        MemberFeedListAdapter adapter = new MemberFeedListAdapter(activity, callback.entries);
        adapter.setClickCallback(new MemberFeedListAdapter.OnItemClickCallback() {
            @Override
            public void onClick(Entry entry) {
                activity.startActivity(BlogActivity.getStartIntent(activity, new Blog(entry)));
                ;
            }
        });
        recyclerView.setAdapter(adapter);
        collapsingToolbarLayout.setTitle(callback.entries.get(0).getAuthor());
    }

    @Subscribe
    public void onChangedFavoriteState(Favorite.ChangedFavoriteState state) {
        if (state.e == null) {
            if (state.action == Favorite.ChangedFavoriteState.Action.ADD) {
                Snackbar.make(coordinatorLayout, R.string.registered_favorite_member, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, R.string.unregistered_favorite_member, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
