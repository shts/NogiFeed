package shts.jp.android.nogifeed.utils

import android.content.Context
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.widget.ImageView

import com.squareup.picasso.Picasso

import shts.jp.android.nogifeed.views.transformations.CircleTransformation

object PicassoHelper {

    private val CIRCLE_TRANSFORMATION = CircleTransformation()

    fun load(context: Context, target: ImageView, url: String) {
        Picasso.with(context)
                .load(url)
                .fit()
                .centerCrop()
                .into(target)
    }

    fun load(target: ImageView, url: String?, @DrawableRes fallback: Int) {
        if (TextUtils.isEmpty(url)) {
            target.setImageResource(fallback)
        } else {
            Picasso.with(target.context)
                    .load(url)
                    .fit()
                    .centerCrop()
                    .into(target)
        }
    }

    fun loadAndCircleTransform(context: Context, target: ImageView, url: String) {
        Picasso.with(context)
                .load(url)
                .fit()
                .centerCrop()
                .transform(CIRCLE_TRANSFORMATION)
                .into(target)
    }

    fun loadAndCircleTransform(target: ImageView, url: String?, @DrawableRes fallback: Int) {
        if (TextUtils.isEmpty(url)) {
            target.setImageResource(fallback)
        } else {
            Picasso.with(target.context)
                    .load(url)
                    .fit()
                    .centerCrop()
                    .transform(CIRCLE_TRANSFORMATION)
                    .into(target)
        }
    }
}
