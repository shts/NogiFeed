package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Member;
import shts.jp.android.nogifeed.widget.ProfileWidgetProvider;

public class ConfigureActivity extends BaseActivity {

    private static final String TAG = ConfigureActivity.class.getSimpleName();
    private static SparseArray<Member> sMembers = new SparseArray<Member>();
    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Logger.v(TAG, "mAppWidgetId(" + mAppWidgetId + ")");
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Logger.w(TAG, "mAppWidgetId is invalidate");
            finish();
        }

    }

    public void setConfigure(Member member) {
        Logger.v(TAG, "in : member(" + member.toString() + ") mAppWidgetId(" + mAppWidgetId + ")");
        sMembers.put(mAppWidgetId, member);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();

        ProfileWidgetProvider.update(getApplicationContext(), mAppWidgetId);
    }

    public static Member getMember(int widgetId) {
        return sMembers.get(widgetId);
    }

    @Override
    public Activity getTrackerActivity() {
        return ConfigureActivity.this;
    }
}
