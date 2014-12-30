package android.shts.jp.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.BlogFragment;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.models.Member;
import android.shts.jp.nogifeed.services.ImageDownloader;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.apache.http.Header;

import java.io.File;

public class BlogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        Bundle bundle = new Bundle();
        Entry entry = getIntent().getParcelableExtra(Entry.KEY);
        bundle.putParcelable(Entry.KEY, entry);

        BlogFragment blogFragment = new BlogFragment();
        blogFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, blogFragment, BlogFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();

        mEntry = entry;
    }
    private Entry mEntry;
    // TODO: for debug
    // TODO: thumnail と raw image の url を取得する処理を追加する
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO: for debug
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ImageDownloader.download(getApplicationContext(), mEntry, new ImageDownloader.ImageDownloadListener() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {

            }
        });
        return super.onOptionsItemSelected(item);
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
