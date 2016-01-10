package shts.jp.android.nogifeed.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.AllFeedListFragment;
import shts.jp.android.nogifeed.fragments.AllMemberGridListFragment;
import shts.jp.android.nogifeed.fragments.CalenderFragment;
import shts.jp.android.nogifeed.fragments.FavoriteMemberFeedListFragment;
import shts.jp.android.nogifeed.fragments.NewsListFragment;
import shts.jp.android.nogifeed.fragments.SettingsFragment;
import shts.jp.android.nogifeed.utils.PreferencesUtils;

public class TopActivity extends AppCompatActivity {

    private static final String TAG = TopActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        final int id = menuItem.getItemId();
                        if (id == getLastSelectedMenuId()) {
                            return false;
                        }
                        setupFragment(id);
                        return false;
                    }
                });
        setupFragment(getLastSelectedMenuId());
    }

    private int getLastSelectedMenuId() {
        return PreferencesUtils.getInt(this, "pre-fragment", R.id.menu_all_feed);
    }

    private void setLastSelectedMenuId(int id) {
        PreferencesUtils.setInt(this, "pre-fragment", id);
    }

    private void setupFragment(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.menu_all_feed:
                fragment = new AllFeedListFragment();
                break;
            case R.id.menu_fav_member_feed:
                fragment = new FavoriteMemberFeedListFragment();
                break;
            case R.id.menu_member:
                fragment = AllMemberGridListFragment.newInstance(
                        AllMemberGridListFragment.Type.ALL_MEMBER);
                break;
            case R.id.menu_news:
                fragment = new NewsListFragment();
                break;
            case R.id.menu_calender:
                fragment = CalenderFragment.newInstance();
                break;
            case R.id.menu_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.menu_about_app:
            case R.id.menu_request:
            case R.id.menu_lisences:
                startActivity(OtherMenuActivity.getStartIntent(this, id));
                return;
            default:
                Log.e(TAG, "failed to change fragment");
                return;
        }
        navigationView.getMenu().findItem(id).setChecked(true);
        toolbar.setTitle(navigationView.getMenu().findItem(id).getTitle());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Log.i(TAG, "id -> " + fragment.getClass().getSimpleName());
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.commit();
        setLastSelectedMenuId(id);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            CalenderFragment calenderFragment =
                    (CalenderFragment) getSupportFragmentManager().findFragmentByTag(
                            CalenderFragment.class.getSimpleName());

            if (calenderFragment != null) {
                if (calenderFragment.isVisible()) {
                    if (calenderFragment.goBack()) {
                        return true;
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
