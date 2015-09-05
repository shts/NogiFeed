package shts.jp.android.nogifeed.views.transformations;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.squareup.picasso.Transformation;

public class BannerTransformation implements Transformation {
    private static final String TAG = BannerTransformation.class.getSimpleName();

    @Override
    public Bitmap transform(Bitmap source) {
//        int size = Math.min(source.getWidth(), source.getHeight());
//        int x = (source.getWidth() - size) / 2;
//        int y = (source.getHeight() - size) / 2;
        int width = (source.getWidth() / 4) * 3;
        int height = source.getHeight();

        float scaleWidth = ((float) width) / 4;
        float scaleHeight = ((float) height) / 4;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix scale = new Matrix();
        scale.postScale(scaleFactor, scaleFactor);

        Bitmap result = Bitmap.createBitmap(source, 0, 0, width, height, scale, false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return BannerTransformation.class.getSimpleName();
    }
}
