package android.shts.jp.nogifeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.AllFeedListFragment;
import android.shts.jp.nogifeed.fragments.AllMemberGridListFragment;
import android.shts.jp.nogifeed.fragments.FavoriteMemberFeedListFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FeedListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);

        AllFeedListFragment allFeedListFragment = new AllFeedListFragment();
//        AllMemberGridListFragment allMemberGridListFragment
//                = new AllMemberGridListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allFeedListFragment, AllFeedListFragment.class.getSimpleName());
        //ft.replace(R.id.container, allMemberGridListFragment, AllMemberGridListFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: go to settings
        // TODO: go to about
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

}
