package shts.jp.android.nogifeed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

public class FromParseReceiver extends BroadcastReceiver {

    private static final String TAG = FromParseReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        try {
            //jsonから値を取り出し
            Bundle extra = intent.getExtras();
            String data = extra.getString("com.parse.Data");
            JSONObject json = new JSONObject(data);
            Log.i(TAG, json.toString());

            //取り出したデータを変数へ
            String url = json.getString("_url");
            String title = json.getString("_title");
            String author = json.getString("_author");

            Log.i(TAG, "url(" + url + ") title(" + title
                    + ") author(" + author + ")");
            BlogUpdateNotification.show(context, url, title, author);

        } catch (JSONException e) {
            Log.e("failed to Parse : ", e.toString());
        }
    }
}
