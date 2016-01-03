package shts.jp.android.nogifeed;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.models.ProfileWidget;
import shts.jp.android.nogifeed.widget.ProfileWidgetProvider;

public class NogiFeedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        parseInstallation();

        ProfileWidgetProvider.initialize(getApplicationContext());
    }

    public void parseInstallation() {

        ParseObject.registerSubclass(Entry.class);
        ParseObject.registerSubclass(Member.class);
        ParseObject.registerSubclass(Favorite.class);
        ParseObject.registerSubclass(NotYetRead.class);
        ParseObject.registerSubclass(ProfileWidget.class);

        // TODO: ここにローカルDBの設定をすると強制終了する
        Parse.enableLocalDatastore(getApplicationContext());

        Parse.initialize(this, BuildConfig.PARSE_API_ID, BuildConfig.PARSE_API_KEY);
        //ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }

}
