package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.ConfigureActivity;
import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.adapters.AllMemberGridListAdapter;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.Members;
import shts.jp.android.nogifeed.providers.FavoriteContentObserver;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.providers.dao.ProfileWidgets;

public class AllMemberGridListFragment extends Fragment {

    private static final String TAG = AllMemberGridListFragment.class.getSimpleName();

    private GridView gridView;
    private AllMemberGridListAdapter gridAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public enum Type {
        UNKNOWN,
        ALL_MEMBER,
        ADD_FAVORITE,
        ADD_WIDGET;
        static Type from(String typeText) {
            if (TextUtils.isEmpty(typeText)) return UNKNOWN;
            if (typeText.equals(ALL_MEMBER.name())) {
                return ALL_MEMBER;
            } else if (typeText.equals(ADD_FAVORITE.name())) {
                return ADD_FAVORITE;
            } else if (typeText.equals(ADD_WIDGET.name())) {
                return ADD_WIDGET;
            }
            return UNKNOWN;
        }
    }

    private Type type;

    private FavoriteContentObserver favoriteContentObserver = new FavoriteContentObserver() {
        @Override
        public void onChangeState(@State int state) {
            if (gridAdapter != null) gridAdapter.notifyDataSetChanged();
        }
    };

    public static AllMemberGridListFragment newInstance(Type type) {
        AllMemberGridListFragment allMemberGridListFragment
                = new AllMemberGridListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type.name());
        allMemberGridListFragment.setArguments(bundle);
        return allMemberGridListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = Type.from(getArguments().getString("type"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_member_list, null);

        if (type == Type.ADD_FAVORITE) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitleTextColor(getResources().getColor(R.color.primary));
            toolbar.setTitle(R.string.choose_member);
            toolbar.setNavigationIcon(R.drawable.ic_clear_purple_700_18dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

        } else if (type == Type.ADD_WIDGET) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitleTextColor(getResources().getColor(R.color.primary));
            toolbar.setTitle(R.string.select_widget_title);
            toolbar.setNavigationIcon(R.drawable.ic_clear_purple_700_18dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

        }

        // SwipeRefreshLayoutの設定
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllMembers();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Member member = (Member) gridView.getItemAtPosition(i);
                switch (type) {
                    case ALL_MEMBER:
                        startActivity(MemberDetailActivity
                                .getStartIntent(getActivity(), member));
                        break;

                    case ADD_FAVORITE:
                        getActivity().setResult(Activity.RESULT_OK);
                        Favorites.toggle(getContext(), member);
                        break;

                    case ADD_WIDGET:
                        if (ProfileWidgets.exist(getContext(), member)) {
                            Toast.makeText(getActivity(), R.string.already_set_same_widget, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ConfigureActivity activity = (ConfigureActivity) getActivity();
                        activity.setConfigure(member);
                        break;
                }
            }
        });
        getAllMembers();
        return view;
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

    private void getAllMembers() {
        subscriptions.add(NogiFeedApiClient.getAllMembers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Members>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Members members) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (members != null) {
                            gridAdapter = new AllMemberGridListAdapter(getActivity(), members);
                            gridView.setAdapter(gridAdapter);
                        }
                    }
                }));
    }
}
