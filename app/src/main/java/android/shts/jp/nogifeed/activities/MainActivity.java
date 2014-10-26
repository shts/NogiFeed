package android.shts.jp.nogifeed.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.AllFeedListFragment;
import android.shts.jp.nogifeed.fragments.BlogFragment;
import android.shts.jp.nogifeed.fragments.ShowcaseFragment;
import android.support.v4.app.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllFeedListFragment allFeedListFragment = new AllFeedListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allFeedListFragment, AllFeedListFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    private void setupActionBar() {
        // TODO: actionbar change color with delete this function.
        ActionBar actionbar = getSupportActionBar();
        Drawable drawable = getResources().getDrawable(R.drawable.ab_solid_nogifeed);
        actionbar.setBackgroundDrawable(drawable);
        actionbar.setLogo(R.drawable.ic_launcher_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: go to settings
        // TODO: go to about
        Intent i = new Intent(this, SettingsActivity2.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment, BlogFragment.class.getSimpleName());
        ft.addToBackStack(AllFeedListFragment.class.getSimpleName());
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            BlogFragment blogFragment =
                    (BlogFragment) getSupportFragmentManager().findFragmentByTag(
                            BlogFragment.class.getSimpleName());

            if (blogFragment != null) {
                if (blogFragment.isVisible()) {
                    if (blogFragment.goBack()) {
                        return true;
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
