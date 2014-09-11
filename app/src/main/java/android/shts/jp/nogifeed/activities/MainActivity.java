package android.shts.jp.nogifeed.activities;

import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.AllFeedListFragment;
import android.shts.jp.nogifeed.fragments.BlogFragment;
import android.support.v4.app.*;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AllFeedListFragment allFeedListFragment = new AllFeedListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allFeedListFragment, AllFeedListFragment.class.getSimpleName());
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

//            AllFeedListFragment allFeedListFragment =
//                    (AllFeedListFragment) getSupportFragmentManager().findFragmentByTag(
//                            AllFeedListFragment.class.getSimpleName());
//
//            if (allFeedListFragment != null) {
//                if (allFeedListFragment.isVisible()) {
//                    return super.onKeyDown(keyCode, event);
//                }
//            }

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
