package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.CheckBox;

import shts.jp.android.nogifeed.R;

public class FavoriteView extends CheckBox {

    public FavoriteView(Context context) {
        super(context, null);
    }

    public FavoriteView(Context context, AttributeSet attr) {
        super(context, attr);

        final Animation pathDown = AnimationUtils.loadAnimation(context, R.anim.path_down);
        final Animation pathCancel = AnimationUtils.loadAnimation(context, R.anim.path_cancel);
        final Animation pathUp = AnimationUtils.loadAnimation(context, R.anim.path_up);
        pathUp.setInterpolator(new CustomInterpolator());

        setOnTouchListener(new OnTouchListener() {
            private boolean cancelled = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startAnimation(pathDown);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!cancelled) {
                            // ボタンからフォーカスが外れた場合
                            if (event.getX() < 0
                                    || v.getWidth() < event.getX()
                                    || event.getY() < 0
                                    || v.getHeight() < event.getY()) {
                                cancelled = true;
                                startAnimation(pathCancel);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (cancelled) {
                            cancelled = false;
                        } else {
                            startAnimation(pathUp);
                            // クリックイベントの処理
                            setChecked(!isChecked());
                        }
                        break;
                }
                return true;
            }
        });
    }

    private class CustomInterpolator implements Interpolator {

        public CustomInterpolator() {}

        @Override
        public float getInterpolation(float input) {
            if (input < 0.4) {
                return (float) ((Math.cos((2.5 * input + 1) * Math.PI) / 2.0f) + 0.5f) * 2f;
            } else if (input < 0.8) {
                return (float) ((Math.cos((2.5 * input + 1) * Math.PI) / 2.0f) + 0.5f) * 1.5f + 0.5f;
            } else {
                return (float) ((Math.cos((2.5 * input + 1) * Math.PI) / 2.0f) + 0.5f) * 1f + 0.5f;
            }
        }
    }

}
