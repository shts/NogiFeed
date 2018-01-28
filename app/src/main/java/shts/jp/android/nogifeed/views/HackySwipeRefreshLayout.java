package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import shts.jp.android.nogifeed.R;

public class HackySwipeRefreshLayout extends SwipeRefreshLayout {

    public HackySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public HackySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.primary);
    }

    /**
     * ActivityやFragmentが開放されるタイミング(onDestroyやonDestroyViewなど)で呼び出す
     * AQUOSなどの一部端末でViewがレンダリングされ続けるバグを回避できる
     */
    public void cleanup() {
        setRefreshing(false);
        destroyDrawingCache();
        clearAnimation();
    }

    private final android.os.Handler refreshHandler = new android.os.Handler(Looper.getMainLooper());
    private final Runnable refreshTask = new Runnable() {
        @Override
        public void run() {
            HackySwipeRefreshLayout.super.setRefreshing(true);
        }
    };

    @Override
    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            refreshHandler.post(refreshTask);
        } else {
            refreshHandler.removeCallbacks(refreshTask);
            super.setRefreshing(refreshing);
        }
    }

}
