package android.shts.jp.nogifeed.activities;

import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.AllFeedListFragment;
import android.shts.jp.nogifeed.fragments.AllMemberGridListFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class MemberListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        AllMemberGridListFragment allMemberGridListFragment
                = new AllMemberGridListFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allMemberGridListFragment, AllFeedListFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

}
