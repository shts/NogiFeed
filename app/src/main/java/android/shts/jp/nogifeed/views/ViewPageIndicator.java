package android.shts.jp.nogifeed.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.shts.jp.nogifeed.R;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ViewPageIndicator extends RadioGroup {

    private int mCount;

    public ViewPageIndicator(Context context) {
        super(context, null);
    }

    public ViewPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void setCount(int count) {

        mCount = count;
        removeAllViews();

        for (int i = 0; i < count; i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setFocusable(false);
            rb.setClickable(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Drawable d = getResources().getDrawable(R.drawable.indicator);
                rb.setButtonDrawable(d);

                LinearLayout.LayoutParams params = generateDefaultLayoutParams();
                params.width = d.getIntrinsicWidth();
                params.height = d.getIntrinsicWidth();

                rb.setLayoutParams(params);
            } else {
                rb.setButtonDrawable(R.drawable.indicator);
            }
            addView(rb);
        }
        setCurrentPosition(-1);
    }

    public void setCurrentPosition(int position) {
        if (position >= mCount) {
            position = mCount - 1;
        }
        if (position < 0) {
            position = mCount > 0 ? 0 : -1;
        }
        if (position >= 0 && position < mCount) {
            RadioButton rb = (RadioButton) getChildAt(position);
            rb.setChecked(true);
        }
    }

}
