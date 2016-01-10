package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.AllMemberGridListFragment;

public class MemberListActivity extends ConfigureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        AllMemberGridListFragment allMemberGridListFragment
                = AllMemberGridListFragment.newInstance(AllMemberGridListFragment.Type.ADD_WIDGET);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allMemberGridListFragment, MemberListActivity.class.getSimpleName());
        ft.commit();
    }

    @Override
    public Activity getTrackerActivity() {
        return MemberListActivity.this;
    }

}
