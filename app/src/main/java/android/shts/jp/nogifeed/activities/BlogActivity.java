package android.shts.jp.nogifeed.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.fragments.BlogFragment;
import android.shts.jp.nogifeed.models.Entry;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

public class BlogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        Entry entry = getIntent().getParcelableExtra(Entry.KEY);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Entry.KEY, entry);

        BlogFragment blogFragment = new BlogFragment();
        blogFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, blogFragment, BlogFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
//        ActionBar actionBar = getSupportActionBar();
//        Drawable drawable = getResources().getDrawable(R.drawable.ab_solid_nogifeed);
//        actionBar.setBackgroundDrawable(drawable);
//        actionBar.setLogo(R.drawable.ic_launcher_white);
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
