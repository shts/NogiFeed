package shts.jp.android.nogifeed;

import android.app.Application;
import android.content.Intent;

import shts.jp.android.nogifeed.receivers.TokenRegistrationService;

public class NogiFeedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, TokenRegistrationService.class));
    }
}
