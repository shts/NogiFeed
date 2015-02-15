package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.AllFeedListFragment;
import shts.jp.android.nogifeed.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, settingsFragment, AllFeedListFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    @Override
    public Activity getTrackerActivity() {
        return SettingsActivity.this;
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
    }
}
