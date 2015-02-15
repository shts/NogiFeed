package shts.jp.android.nogifeed.fragments.tablet;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import shts.jp.android.nogifeed.R;

/**
 * For tablet layout.
 */
public class AllMemberListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAllMember();
    }

    private void getAllMember() {

        boolean ret = shts.jp.android.nogifeed.utils.JsoupUtils.getAllMembers(getActivity(), new shts.jp.android.nogifeed.utils.JsoupUtils.GetMemberListener() {
            @Override
            public void onSuccess(List<shts.jp.android.nogifeed.models.Member> memberList) {
                setupAdapter(memberList);
            }

            @Override
            public void onFailed() {
                // Show error toast
                Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                        Toast.LENGTH_SHORT).show();
            }
        });

        if (!ret) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAdapter(List<shts.jp.android.nogifeed.models.Member> memberList) {
        memberList.add(0, new shts.jp.android.nogifeed.models.Member(shts.jp.android.nogifeed.utils.UrlUtils.FEED_ALL_URL, "All Members"));
        setListAdapter(new shts.jp.android.nogifeed.adapters.AllMemberListAdapter(getActivity(), memberList));
    }
}
