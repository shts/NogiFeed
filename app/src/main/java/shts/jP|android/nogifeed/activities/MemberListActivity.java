package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.legacy.AllMemberGridListFragment;

public class MemberListActivity extends ConfigureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        AllMemberGridListFragment allMemberGridListFragment
                = new AllMemberGridListFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allMemberGridListFragment, MemberListActivity.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    @Override
    public Activity getTrackerActivity() {
        return MemberListActivity.this;
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

}
