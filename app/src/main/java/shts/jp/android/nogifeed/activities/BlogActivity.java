package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.entities.Blog;
import shts.jp.android.nogifeed.fragments.BlogFragment;

public class BlogActivity extends BaseActivity {

    public static Intent getStartIntent(Context context, Blog blog) {
        Intent intent = new Intent(context, BlogActivity.class);
        intent.putExtra("blog", blog);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        final Blog blog = getIntent().getParcelableExtra("blog");
        BlogFragment blogFragment = BlogFragment.newInstance(blog);

        setupToolbar(blog);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, blogFragment, BlogFragment.class.getSimpleName());
        ft.commit();
    }

    private void setupToolbar(@Nullable Blog blog) {
        if (blog == null) return;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) return;

        toolbar.setTitle(blog.getTitle());
        toolbar.setSubtitle(blog.getAuthor());
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public Activity getTrackerActivity() {
        return BlogActivity.this;
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
