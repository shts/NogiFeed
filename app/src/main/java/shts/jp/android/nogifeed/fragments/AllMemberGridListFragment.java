package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.ConfigureActivity;
import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.activities.MemberDetailActivity2;
import shts.jp.android.nogifeed.adapters.AllMemberGridListAdapter;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.ProfileWidget;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;

public class AllMemberGridListFragment extends Fragment {

    private static final String TAG = AllMemberGridListFragment.class.getSimpleName();

    private GridView gridView;
    private AllMemberGridListAdapter gridAdapter;

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
        }

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Member member = (Member) gridView.getItemAtPosition(i);
                switch (type) {
                    case ALL_MEMBER:
                        startActivity(MemberDetailActivity2
                                .getStartIntent(getActivity(), member));
//                        startActivity(MemberDetailActivity
//                                .getStartIntent(getActivity(), member.getObjectId()));
                        break;

                    case ADD_FAVORITE:
                        Favorite.toggle(member.getObjectId());
                        break;

                    case ADD_WIDGET:
                        if (ProfileWidget.exist(member.getObjectId())) {
                            Toast.makeText(getActivity(), R.string.already_set_same_widget, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ConfigureActivity activity = (ConfigureActivity) getActivity();
                        activity.setConfigure(member);
                        break;
                }
            }
        });
        Member.all();
        return view;
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

    @Subscribe
    public void onGotAllMembers(Member.GetMembersCallback callback) {
        if (callback.e == null && callback.members != null) {
            gridAdapter = new AllMemberGridListAdapter(getActivity(), callback.members);
            gridView.setAdapter(gridAdapter);
        }
    }

    @Subscribe
    public void onChangedFavoriteState(Favorite.ChangedFavoriteState state) {
        if (gridAdapter != null) gridAdapter.notifyDataSetChanged();
    }

}
