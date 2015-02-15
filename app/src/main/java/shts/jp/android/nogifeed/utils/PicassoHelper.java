package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import shts.jp.android.nogifeed.views.transformations.CircleTransformation;

public class PicassoHelper {

    private static final CircleTransformation CIRCLE_TRANSFORMATION = new CircleTransformation();

    public static void load(Context context, ImageView target, String url) {
        Picasso.with(context)
                .load(url)
                .into(target);
    }

    public static void loadAndCircleTransform(Context context, ImageView target, String url) {
        Picasso.with(context)
                .load(url)
                .transform(CIRCLE_TRANSFORMATION)
                .into(target);
    }

}
