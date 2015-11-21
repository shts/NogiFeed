package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.ConfigureActivity;
import shts.jp.android.nogifeed.adapters.AllMemberGridListAdapter;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.legacy.ProfileWidget;

public class AllMemberGridListFragment extends Fragment {

    private static final String TAG = AllMemberGridListFragment.class.getSimpleName();

    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_member_list, null);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Member member = (Member) gridView.getItemAtPosition(i);
                if (ProfileWidget.exist(
                        getActivity().getApplicationContext(), member.getRssUrl())) {
                    Toast.makeText(getActivity(), "already set same widget", Toast.LENGTH_SHORT).show();
                    Logger.w(TAG, "has already set widget feedUrl(" + member.getRssUrl() + ")");
                    return;
                }
                ConfigureActivity activity = (ConfigureActivity) getActivity();
                activity.setConfigure(member);

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupAdapter();
    }

    private void setupAdapter() {
        Member.getQuery().findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> memberList, ParseException e) {
                AllMemberGridListAdapter gridAdapter = new AllMemberGridListAdapter(getActivity(), memberList);
                gridView.setAdapter(gridAdapter);
            }
        });
    }

}
