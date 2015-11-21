package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.BlogFragment;
import shts.jp.android.nogifeed.models.Entry;

public class BlogActivity extends BaseActivity {

    public static Intent getStartIntent(Context context, String entryObjectId) {
        Intent intent = new Intent(context, BlogActivity.class);
        intent.putExtra(Entry.KEY, entryObjectId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        String entryObjectId = getIntent().getStringExtra(Entry.KEY);
        BlogFragment blogFragment = BlogFragment.newBlogFragment(entryObjectId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, blogFragment, BlogFragment.class.getSimpleName());
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
