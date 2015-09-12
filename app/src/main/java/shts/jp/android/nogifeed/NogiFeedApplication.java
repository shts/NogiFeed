package shts.jp.android.nogifeed;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

import shts.jp.android.nogifeed.widget.ProfileWidgetProvider;

public class NogiFeedApplication extends Application {

    public NogiFeedApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parseInstallation();

        ProfileWidgetProvider.initialize(getApplicationContext());
    }

    public void parseInstallation() {
        Parse.initialize(this, BuildConfig.PARSE_API_ID, BuildConfig.PARSE_API_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
