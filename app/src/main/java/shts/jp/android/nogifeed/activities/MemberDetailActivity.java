package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.MemberDetailFragment;
import shts.jp.android.nogifeed.models.BlogEntry;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Member;

public class MemberDetailActivity extends BaseActivity {

    private Drawable mActionBarDrawable;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_member_detail);

        Intent i = getIntent();
        Bundle bundle = new Bundle();
        // Maybe not use
        Entry entry = i.getParcelableExtra(Entry.KEY);
        if (entry != null) {
            // intent from AllFeedListFragment
            bundle.putParcelable(Entry.KEY, entry);
            setupActionBar(entry.name);
        }
        Member member = i.getParcelableExtra(Member.KEY);
        if (member != null) {
            // intent from MemberGridListFragment
            bundle.putParcelable(Member.KEY, member);
            setupActionBar(member.name);
        }
        BlogEntry blogEntry = i.getParcelableExtra(BlogEntry.KEY);
        if (blogEntry != null) {
            // intent from AllFeedListFragment
            bundle.putParcelable(BlogEntry.KEY, blogEntry);
            setupActionBar(blogEntry.author);
        }

        MemberDetailFragment memberDetailFragment = new MemberDetailFragment();
        memberDetailFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, memberDetailFragment, MemberDetailFragment.class.getSimpleName());
        ft.commit();
    }

    @Override
    public Activity getTrackerActivity() {
        return MemberDetailActivity.this;
    }

    private void setupActionBar(String name) {
        mToolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle(name);

        mActionBarDrawable = getResources().getDrawable(R.drawable.ab_solid_nogifeed);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mToolbar.setBackgroundDrawable(mActionBarDrawable);
        } else {
            mToolbar.setBackground(mActionBarDrawable);
        }
    }

    public void setActionBarDrawableAlpha(int alpha) {
        mActionBarDrawable.setAlpha(alpha);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mToolbar.setBackgroundDrawable(mActionBarDrawable);
        } else {
            mToolbar.setBackground(mActionBarDrawable);
        }
    }
}
