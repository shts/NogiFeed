package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.shts.jp.nogifeed.views.transformations.CircleTransformation;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by saitoushouta on 2014/09/27.
 */
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

    public static void loadThumbnail(Context context, ImageView target, String url) {
        Picasso.with(context)
                .load(buildSiteThumbnailUrl(url))
//                .placeholder(R.drawable.loading)
                .into(target);
    }

    public static final String buildSiteThumbnailUrl(String linkUrl) {
        return "http://capture.heartrails.com/256x256?" + linkUrl;
    }

}
