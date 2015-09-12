package shts.jp.android.nogifeed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import shts.jp.android.nogifeed.BuildConfig;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.UnRead;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

public class FromParseReceiver extends BroadcastReceiver {

    private static final String TAG = FromParseReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(TAG, "onReceive");
        try {
            //jsonから値を取り出し
            Bundle extra = intent.getExtras();
            String data = extra.getString("com.parse.Data");
            JSONObject json = new JSONObject(data);
            Log.i(TAG, json.toString());

            //取り出したデータを変数へ
            final String url = json.getString("_url");
            final String title = json.getString("_title");
            final String author = json.getString("_author");

            Log.d(TAG, "url(" + url + ") title(" + title
                    + ") author(" + author + ")");

            // save unread article
            UnRead.newUnReadArticle(context, url);

            // show notification
            BlogUpdateNotification.show(context, url, title, author);

            if (BuildConfig.DEBUG) {
                UnRead.dump(context);
            }

        } catch (JSONException e) {
            Logger.e("failed to Parse : ", e.toString());
        }
    }
}
