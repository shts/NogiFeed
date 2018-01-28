package shts.jp.android.nogifeed.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        // コンストラクタを追加しないとクラスを見つけることができず強制終了する
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionsMenu child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionsMenu child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
