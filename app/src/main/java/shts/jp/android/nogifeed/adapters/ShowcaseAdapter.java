package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class ShowcaseAdapter<T> extends PagerAdapter {

    private static final String TAG = ShowcaseAdapter.class.getSimpleName();

    private final List<T> mList;
    private final Context mContext;

    public ShowcaseAdapter(Context context, List<T> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((View)object).equals(view);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View v = (View) object;
        container.removeView(v);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View instantiateItem = (View) getInstantiateItem(container, mList.get(position));
        container.addView(instantiateItem);
        return instantiateItem;
    }

    protected abstract Object getInstantiateItem(ViewGroup container, T item);
}
