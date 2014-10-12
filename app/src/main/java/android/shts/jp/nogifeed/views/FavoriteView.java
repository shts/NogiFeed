package android.shts.jp.nogifeed.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FavoriteView extends Button {

    private static final int UNFAVORITE = 0;
    private static final int FAVORITE = 1;

    private boolean mFavorite = false;

    public FavoriteView(Context context) {
        super(context);
    }

    public FavoriteView(Context context, AttributeSet attr) {
        super(context, attr);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

}
