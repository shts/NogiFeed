package android.shts.jp.nogifeed.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.ShowcaseFragment;
import android.shts.jp.nogifeed.models.Entry;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
//        actionBar.setDisplayShowTitleEnabled(false);

        mActionBarDrawable = getResources().getDrawable(R.drawable.ab_solid_nogifeed);
        actionBar.setBackgroundDrawable(mActionBarDrawable);
    }

    public void setActionBarDrawableAlpha(int alpha) {
        mActionBarDrawable.setAlpha(alpha);
        getSupportActionBar().setBackgroundDrawable(mActionBarDrawable);
    }

}
