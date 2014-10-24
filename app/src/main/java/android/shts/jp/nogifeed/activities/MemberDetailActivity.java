package android.shts.jp.nogifeed.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.AllFeedListFragment;
import android.shts.jp.nogifeed.fragments.BlogFragment;
import android.shts.jp.nogifeed.fragments.ShowcaseFragment;
import android.shts.jp.nogifeed.models.Entry;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Window;

public class MemberDetailActivity extends ActionBarActivity {

    private Drawable mActionBarDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        setupActionBar();

        Intent i = getIntent();
        Entry entry = i.getParcelableExtra(Entry.KEY);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Entry.KEY, entry);

        ShowcaseFragment showcaseFragment = new ShowcaseFragment();
        showcaseFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, showcaseFragment, ShowcaseFragment.class.getSimpleName());
        ft.commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        // hide actionbar titile
        actionBar.setTitle("");
        //actionBar.setLogo(R.drawable.unfavorite_normal_72);

        mActionBarDrawable = getResources().getDrawable(R.drawable.ab_solid_nogifeed);
        actionBar.setBackgroundDrawable(mActionBarDrawable);
    }

    public void setActionBarDrawableAlpha(int alpha) {
        mActionBarDrawable.setAlpha(alpha);
        getSupportActionBar().setBackgroundDrawable(mActionBarDrawable);
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment, BlogFragment.class.getSimpleName());
        ft.addToBackStack(ShowcaseFragment.class.getSimpleName());
        ft.commit();

        // TODO: issue, actionbar change alpha at touch BlogFragment.
        // actionbar background color change.
        mActionBarDrawable.setAlpha(255);
        getSupportActionBar().setBackgroundDrawable(mActionBarDrawable);
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
