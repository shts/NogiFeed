package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.BlogFragment;
import shts.jp.android.nogifeed.fragments.NewsBrowseFragment;
import shts.jp.android.nogifeed.models.News;

public class NewsBrowseActivity extends BaseActivity {

    private static final String TAG = NewsBrowseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_browse);

        final News news = getIntent().getParcelableExtra(News.KEY);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(News.KEY, news);

        NewsBrowseFragment newsBrowseFragment = new NewsBrowseFragment();
        newsBrowseFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, newsBrowseFragment, BlogFragment.class.getSimpleName());
        ft.commit();

        setupActionBar();
    }

    @Override
    public Activity getTrackerActivity() {
        return NewsBrowseActivity.this;
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }
}
