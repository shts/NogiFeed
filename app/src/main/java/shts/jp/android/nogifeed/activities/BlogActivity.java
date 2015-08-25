package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.fragments.BlogFragment;
import shts.jp.android.nogifeed.models.BlogEntry;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.services.ImageDownloader;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

public class BlogActivity extends BaseActivity {

    private BlogEntry mBlogEntry;
    private final BlogFragment mBlogFragment = new BlogFragment();

    public static Intent getStartIntent(Context context, BlogEntry blogEntry) {
        Intent intent = new Intent(context, BlogActivity.class);
        intent.putExtra(BlogEntry.KEY, blogEntry);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        Bundle bundle = new Bundle();

        mBlogEntry = getIntent().getParcelableExtra(BlogEntry.KEY);
        bundle.putParcelable(BlogEntry.KEY, mBlogEntry);

        String blogUrl = getIntent().getStringExtra(BlogUpdateNotification.KEY);
        bundle.putString(BlogUpdateNotification.KEY, blogUrl);

        mBlogFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mBlogFragment, BlogFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    @Override
    public Activity getTrackerActivity() {
        return BlogActivity.this;
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
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
