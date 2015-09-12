package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.NewsBrowseActivity;
import shts.jp.android.nogifeed.adapters.ShowcaseAdapter;
import shts.jp.android.nogifeed.api.AsyncBannerClient;
import shts.jp.android.nogifeed.api.AsyncBannerResponseHandler;
import shts.jp.android.nogifeed.entities.Banner;
import shts.jp.android.nogifeed.utils.PicassoHelper;

public class BannerShowcase extends FrameLayout {

    private static final String TAG = BannerShowcase.class.getSimpleName();

    private ViewPager mViewPager;
    private ViewPageIndicator mViewPageIndicator;
    private FavoriteView mFavoriteCheckbox;

    public BannerShowcase(Context context) {
        super(context, null);

        LayoutInflater.from(context).inflate(R.layout.showcase, this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPageIndicator = (ViewPageIndicator) findViewById(R.id.indicator);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mViewPageIndicator.setCurrentPosition(position);
            }
        });
        mFavoriteCheckbox = (FavoriteView) findViewById(R.id.favorite);
        mFavoriteCheckbox.setVisibility(View.GONE);

        int height = (int) ( /*240*/ 160 * getResources().getDisplayMetrics().density);
        setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height));

        updateBanners();
    }

    public void updateBanners() {
        AsyncBannerClient.getBannerList(getContext(), new AsyncBannerResponseHandler() {
            @Override
            public void onFinish(ArrayList<Banner> bannerList) {
                setupAdapter(bannerList);
            }
        });
    }

    private void setupAdapter(List<Banner> bannerList) {
        mViewPager.setAdapter(new ShowcaseAdapter<Banner>(getContext(), bannerList) {
            @Override
            protected Object getInstantiateItem(final ViewGroup container, final Banner banner) {
                final Context context = getContext();
                ImageView iv = new ImageView(context);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(
                                NewsBrowseActivity.createIntent(context, banner));
                    }
                });
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                PicassoHelper.load(context, iv, banner.thumurl);
                container.addView(iv);
                return iv;
            }
        });
    }
}
