package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.MemberFeedListAdapter2;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;

public class MemberEntryListFragment extends Fragment {

    private static final String TAG = MemberEntryListFragment.class.getSimpleName();

    public static MemberEntryListFragment newInstance(String memberObjectId) {
        Bundle bundle = new Bundle();
        bundle.putString("memberObjectId", memberObjectId);
        MemberEntryListFragment memberEntryListFragment = new MemberEntryListFragment();
        memberEntryListFragment.setArguments(bundle);
        return memberEntryListFragment;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_member_entry_list, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);

        Entry.findById(30, 0, getArguments().getString("memberObjectId"));
        return view;
    }

    @Subscribe
    public void onGotAllEntries(Entry.GotAllEntryCallback.FindById callback) {
        Log.d(TAG, "onGotAllEntries");
        if (callback.hasError()) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        recyclerView.setAdapter(new MemberFeedListAdapter2(getActivity(), callback.entries));
    }
}
