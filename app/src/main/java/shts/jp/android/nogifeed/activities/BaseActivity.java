package shts.jp.android.nogifeed.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import shts.jp.android.nogifeed.utils.TrackerUtils;

public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTracker();
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(getTrackerActivity());
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(getTrackerActivity());
    }

    public abstract Activity getTrackerActivity();

    public Tracker getTracker() {
        return TrackerUtils.getTracker(getApplicationContext());
    }
}
