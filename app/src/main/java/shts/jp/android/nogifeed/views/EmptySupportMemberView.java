package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import shts.jp.android.nogifeed.R;

public class EmptySupportMemberView extends LinearLayout {

    public EmptySupportMemberView(Context context) {
        super(context, null);
    }

    public EmptySupportMemberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_empty_favorite, null);

    }

    private void findViews(View view) {

    }

}
