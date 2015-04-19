package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.lang.reflect.Field;

import shts.jp.android.nogifeed.common.Logger;

/**
 */
public class FixedGridView extends GridView {

    private ListAdapter mAdapter;

    public FixedGridView(Context context) {
        this(context, null);
    }

    public FixedGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int num = getNumColumns();
        float density = getContext().getResources().getDisplayMetrics().density;
        int px = Math.round(3 * density);
        int width  = px * num; // px * カラム数
        int height = px * (int) Math.ceil((float) mAdapter.getCount() / (float) num); // px * 行数
        Logger.v("onMeasure", "in : num(" + num + ") density(" + density + ") px(" + px
                + ") width(" + width + ") height(" + height + ")");
        setMeasuredDimension(width, height);
    }

    @Override
    public int getNumColumns() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return super.getNumColumns();
        } else {
            try {
                Field numColumns = getClass().getSuperclass().getDeclaredField("mNumColumns");
                numColumns.setAccessible(true);
                return numColumns.getInt(this);
            } catch (Exception e) {
                return 1;
            }
        }
    }
}
