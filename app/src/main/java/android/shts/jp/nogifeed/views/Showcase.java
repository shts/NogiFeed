package android.shts.jp.nogifeed.views;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

public class Showcase extends FrameLayout {

    private ViewPager mViewPager;
    private ViewPageIndicator mViewPageIndicator;
    private final List<String> mImageUrls;

    public Showcase(Context context, List<String> imageUrls) {
        this(context, null, imageUrls);
    }

    public Showcase(Context context, AttributeSet attrs, List<String> imageUrls) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.showcase, this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPageIndicator = (ViewPageIndicator) findViewById(R.id.indicator);
        mImageUrls = imageUrls;
    }

    public void setImageResources(int[] resIds) {
        CustomPageAdapter adapter = new CustomPageAdapter(getContext(), resIds, mImageUrls);
        mViewPager.setAdapter(adapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mViewPageIndicator.setCurrentPosition(position);
            }
        });
        mViewPageIndicator.setCount(adapter.getCount());
    }

    public class CustomPageAdapter extends PagerAdapter {

        Context mContext;
        int[] mResIds;
        private final List<String> mImageUrls;

        public CustomPageAdapter(Context context, int[] resIds, List<String> imageUrls) {
            mContext = context;
            mResIds = resIds;
            mImageUrls = imageUrls;
        }

        @Override
        public int getCount() {
//            return mResIds.length;
            return mImageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return ((View)o).equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(mContext);
//            iv.setImageResource(mResIds[position]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // get image from using Picasso
            PicassoHelper.load(mContext, iv, mImageUrls.get(position));
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }
    }

}
