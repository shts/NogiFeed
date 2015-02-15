package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity for deep link from browser application.
 */
public class DeepLinkAcivity extends shts.jp.android.nogifeed.activities.BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(this, shts.jp.android.nogifeed.MyActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        finish();
    }

    @Override
    public Activity getTrackerActivity() {
        return DeepLinkAcivity.this;
    }
}
