package android.shts.jp.nogifeed;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class NogiFeedApplication extends Application {

    public NogiFeedApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getString(R.string.api_id), getString(R.string.api_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
