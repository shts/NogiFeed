package shts.jp.android.nogifeed.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.BlogFragment;
import shts.jp.android.nogifeed.models.Entry;

public class BlogActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context, Entry entry) {
        Intent intent = new Intent(context, BlogActivity.class);
        intent.putExtra("entry", entry);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        Entry entry = getIntent().getParcelableExtra("entry");
        BlogFragment blogFragment = BlogFragment.newInstance(entry);

        setupToolbar(entry);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, blogFragment, BlogFragment.class.getSimpleName());
        ft.commit();
    }

    private void setupToolbar(@Nullable Entry entry) {
        if (entry == null) return;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) return;

        toolbar.setTitle(entry.getTitle());
        toolbar.setSubtitle(entry.getMemberName());
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    public void onBackPressed() {
        BlogFragment blogFragment =
                (BlogFragment) getSupportFragmentManager().findFragmentByTag(
                        BlogFragment.class.getSimpleName());

        if (blogFragment != null) {
            if (blogFragment.isVisible()) {
                if (blogFragment.onBackPressed()) return;
            }
        }
        super.onBackPressed();
    }
}
