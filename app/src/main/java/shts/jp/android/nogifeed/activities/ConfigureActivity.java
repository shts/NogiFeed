package shts.jp.android.nogifeed.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;

/**
 * TODO: Widget用のActivity
 */
public class ConfigureActivity extends AppCompatActivity {

    private static final String TAG = ConfigureActivity.class.getSimpleName();
    private static SparseArray<Member> members = new SparseArray<>();
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Logger.v(TAG, "appWidgetId(" + appWidgetId + ")");
        }

        // If they gave us an intent without the widget id, just bail.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Logger.w(TAG, "appWidgetId is invalidate");
            finish();
        }

    }

    public void setConfigure(Member member) {
        Logger.v(TAG, "in : member(" + member.toString() + ") appWidgetId(" + appWidgetId + ")");
        members.put(appWidgetId, member);

        //ProfileWidgetProvider.update(getApplicationContext(), appWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public static Member getMember(int widgetId) {
        return members.get(widgetId);
    }

}
