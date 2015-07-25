package shts.jp.android.nogifeed;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.parse.Parse;
import com.parse.ParseInstallation;

import org.json.JSONException;
import org.json.JSONObject;

import shts.jp.android.nogifeed.widget.ProfileWidgetProvider;

public class NogiFeedApplication extends Application {

    public NogiFeedApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parseInstllation();

        ProfileWidgetProvider.initialize(getApplicationContext());

        if (BuildConfig.DEBUG) {
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        // http://blog.nogizaka46.com/mai.shiraishi/2015/07/024287.php
                        JSONObject json = new JSONObject();
                        json.put("_url", "http://blog.nogizaka46.com/mai.shiraishi/2015/07/024287.php");
                        json.put("_title", "debug_title");
                        json.put("_author", "debug_auther");

                        Intent i = new Intent("android.shts.jp.nogifeed.UPDATE_STATUS");
                        i.putExtra("com.parse.Data", json.toString());
                        sendBroadcast(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new IntentFilter("jp.shts.nogifeed.action.DEBUG"));
        }
    }

    public void parseInstllation() {
        Parse.initialize(this, BuildConfig.PARSE_API_ID, BuildConfig.PARSE_API_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
