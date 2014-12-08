package android.shts.jp.nogifeed.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.fragments.AllFeedListFragment;
import android.shts.jp.nogifeed.fragments.FavoriteMemberFeedListFragment;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.utils.AtomRssParser;
import android.shts.jp.nogifeed.utils.JsoupUtils;
import android.shts.jp.nogifeed.views.PagerSlidingTabStrip;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 2.3.7 の端末で NoSuchMethodError が発生してアプリがクラッシュする
 * 下記Viewを参照して独自実装
 */
//import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

// TODO: toolbar のエレベーションをあげる(標準のアプリには影がある)
public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private SystemBarTintManager mTintManager;
    private NogiBasePageAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();
        setupStatusBar();
        setupViewPager();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

    private void setupStatusBar() {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom navigation bar resource
        tintManager.setNavigationBarTintResource(R.color.nogifeed_dark);
    }

    private void setupViewPager() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPageAdapter = new NogiBasePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        mPagerSlidingTabStrip.setTextColor(Color.WHITE);
    }

    public static class NogiBasePageAdapter extends FragmentPagerAdapter {

        private static final int INDEX_ALL_FEED = 0;
        private static final int INDEX_FAV_FEED = 1;

        private static final Fragment[] PAGES = {
            new AllFeedListFragment(),
            new FavoriteMemberFeedListFragment(),
        };

        public NogiBasePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return PAGES[i];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPageTitleFrom(position);
        }

        private String getPageTitleFrom(int position) {
            switch (position) {
                case INDEX_ALL_FEED:
                    return "All Member";
                case INDEX_FAV_FEED:
                    return "Fav Member";
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGES.length;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: debug complate! all feed url is valid! add follow function at Model'Member class.
//        JsoupUtils.getAllMemberFeedUrl(new JsoupUtils.AllMemberFeedRequestHandler() {
//            @Override
//            public void onSuccess(List<String> feedUrls) {
//
//                AsyncHttpClient client = new AsyncHttpClient();
//
//                for (String url : feedUrls) {
//                    final String feedUrl = url;
//                    Logger.d(TAG, "getAllMemberFeedUrl() : url(" + feedUrl + ")");
////                    AsyncRssClient.read(getApplicationContext(), feedUrl, new RssClientListener() {
////                        @Override
////                        public void onSuccess(int statusCode, Header[] headers, Entries entries) {
////                            Logger.i(TAG, "valid feed url : feedUrl(" + feedUrl + ")" );
////                        }
////
////                        @Override
////                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
////                            Logger.e(TAG, "failed to connection url : statusCode(" + statusCode
////                            + ") url(" + feedUrl + ")");
////                        }
////                    });
//                    client.get(feedUrl, new AsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                            Logger.i(TAG, "valid feed url : feedUrl(" + feedUrl + ")" );
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                            Logger.e(TAG, "Invalid feed url : statusCode(" + statusCode + ")" );
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailed() {
//                Logger.d(TAG, "getAllMemberFeedUrl() : failed to get all member feed list.");
//            }
//        });
        // TODO: go to settings
//        Intent i = new Intent(this, AboutActivity.class);
//        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
