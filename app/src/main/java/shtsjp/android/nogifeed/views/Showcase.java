package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.ShowcaseAdapter;
import shts.jp.android.nogifeed.utils.PicassoHelper;
import shts.jp.android.nogifeed.utils.TrackerUtils;

public class Showcase extends FrameLayout {
    private static final String TAG = Showcase.class.getSimpleName();

    private ViewPager mViewPager;
    private ViewPageIndicator mViewPageIndicator;
    private List<String> mImageUrls;
    private FavoriteView mFavoriteCheckbox;
    private FavoriteChangeListener mListener;

    private ShowcaseAdapter<String> mShowcaseAdapter;

    public interface FavoriteChangeListener {
        public void onCheckedChanged(CompoundButton compoundButton, boolean b);
    }

    public Showcase(Context context, List<String> imageUrls) {
        this(context, null, imageUrls);
    }

    public Showcase(Context context, AttributeSet attrs, List<String> imageUrls) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.showcase, this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPageIndicator = (ViewPageIndicator) findViewById(R.id.indicator);
        mFavoriteCheckbox = (FavoriteView) findViewById(R.id.favorite);
        mFavoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mListener != null) {
                    mListener.onCheckedChanged(compoundButton, b);
                }
                TrackerUtils.sendTrack(getContext(), TAG,
                        "OnClicked", "-> Favorite : " + "isFavorite(" + b + ")");
            }
        });
        mImageUrls = imageUrls;
        setupAdapter();
    }

    public void setOnCheckedChangeListener(FavoriteChangeListener listener) {
        mListener = listener;
    }

    public void setFavorite(boolean isChecked) {
        mFavoriteCheckbox.setChecked(isChecked);
    }

    private void setupAdapter() {
        mShowcaseAdapter = new ShowcaseAdapter<String>(getContext(), mImageUrls) {
            @Override
            protected Object getInstantiateItem(ViewGroup container, String item) {
                final Context context = getContext();
                ImageView iv = new ImageView(context);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                PicassoHelper.load(context, iv, item);
                container.addView(iv);
                return iv;
            }
        };
        mViewPager.setAdapter(mShowcaseAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mViewPageIndicator.setCurrentPosition(position);
            }
        });
        mViewPageIndicator.setCount(mShowcaseAdapter.getCount());
    }

}
