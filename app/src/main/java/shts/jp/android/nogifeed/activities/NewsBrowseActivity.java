package shts.jp.android.nogifeed.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.fragments.BlogFragment;
import shts.jp.android.nogifeed.fragments.NewsBrowseFragment;

public class NewsBrowseActivity extends AppCompatActivity {

    private static final String TAG = NewsBrowseActivity.class.getSimpleName();

    public static Intent getStartIntent(final Context context, News news) {
        Intent intent = new Intent(context, NewsBrowseActivity.class);
        intent.putExtra("news", news);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_browse);

        final News news = getIntent().getParcelableExtra("news");
        NewsBrowseFragment newsBrowseFragment
                = NewsBrowseFragment.newInstance(news);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, newsBrowseFragment, BlogFragment.class.getSimpleName());
        ft.commit();
    }

}
