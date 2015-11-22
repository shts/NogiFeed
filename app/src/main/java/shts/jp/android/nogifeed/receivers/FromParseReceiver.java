package shts.jp.android.nogifeed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

public class FromParseReceiver extends BroadcastReceiver {

    private static final String TAG = FromParseReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(TAG, "onReceive");
        // TODO: change ref object id
        try {
            //jsonから値を取り出し
            Bundle extra = intent.getExtras();
            String data = extra.getString("com.parse.Data");
            JSONObject json = new JSONObject(data);
            Log.i(TAG, json.toString());

            //取り出したデータを変数へ
//            final String url = json.getString("_url");
//            final String title = json.getString("_title");
//            final String author = json.getString("_author");
            final String entryObjectId = json.getString("_objectId");

            //TODO: save unread article
            //UnRead.newUnReadArticle(context, url);
            NotYetRead.add(entryObjectId);

            // show notification
            BlogUpdateNotification.show(context, entryObjectId);

        } catch (JSONException e) {
            Logger.e("failed to Parse : ", e.toString());
        }
    }
}
