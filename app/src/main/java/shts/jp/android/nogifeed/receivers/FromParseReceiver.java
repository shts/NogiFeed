package shts.jp.android.nogifeed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Blog;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;
import shts.jp.android.nogifeed.views.notifications.NewsUpdateNotification;

public class FromParseReceiver extends BroadcastReceiver {

    private static final String TAG = FromParseReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            Logger.w(TAG, "action is empty");
            return;
        }

        try {
            //jsonから値を取り出し
            Bundle extra = intent.getExtras();
            String data = extra.getString("com.parse.Data");
            JSONObject json = new JSONObject(data);

            if (action.equals("android.shts.jp.nogifeed.UPDATE_STATUS2")) {
                Blog blog = new Blog(json);

                // 未読記事としてマーキング
                NotYetRead.add(blog.getEntryObjectId());

                // Notification通知
                new BlogUpdateNotification(context).show(blog);

            } else if (action.equals("android.shts.jp.nogifeed.UPDATE_NEWS")) {
                // ニュース通知の場合
                final int category = Integer.parseInt(json.getString("_category"));
                final String title = json.getString("_title");
                final String url = json.getString("_url");
                // Notification通知
                new NewsUpdateNotification(context).show(new News(null, category, url, title));

            } else {
                Logger.w(TAG, "illegal action received : action(" + action + ")");
            }
        } catch (JSONException e) {
            Logger.w(TAG, "illegal data received");
        }
    }

}
