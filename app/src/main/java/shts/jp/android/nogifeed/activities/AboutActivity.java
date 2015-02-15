package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import shts.jp.android.nogifeed.R;

public class AboutActivity extends shts.jp.android.nogifeed.activities.BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        shts.jp.android.nogifeed.fragments.AboutFragment aboutFragment = new shts.jp.android.nogifeed.fragments.AboutFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, aboutFragment, shts.jp.android.nogifeed.fragments.AllFeedListFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public Activity getTrackerActivity() {
        return AboutActivity.this;
    }
}
