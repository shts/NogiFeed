package shts.jp.android.nogifeed.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import shts.jp.android.nogifeed.BuildConfig;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.providers.NogiFeedContent;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.utils.UrlUtils;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

public class FromParseReceiver extends BroadcastReceiver {

    private static final String TAG = FromParseReceiver.class.getSimpleName();
    private final android.os.Handler mHandler = new android.os.Handler();

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
            updateUnReadArticle(context, url);

            // show notification
            BlogUpdateNotification.show(context, url, title, author);

            if (BuildConfig.DEBUG) {
                DataStoreUtils.allUnReadArticle(context);
            }

        } catch (JSONException e) {
            Logger.e("failed to Parse : ", e.toString());
        }
    }

    private void updateUnReadArticle(final Context context, final String article) {
        Logger.v(TAG, "updateUnReadArticle(Context, String) : article(" + article + ")");

        final boolean hasAlreadyReadArticle =
                DataStoreUtils.hasAlreadyRead(context, article);
        if (hasAlreadyReadArticle) {
            Logger.v(TAG, "has already read a article");
            return;
        }

        final String feedUrl = UrlUtils.getMemberFeedUrl(article);
        final ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.UnRead.KEY_FEED_URL, feedUrl);
        cv.put(NogiFeedContent.UnRead.KEY_ARTICLE_URL, article);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                context.getContentResolver().insert(NogiFeedContent.UnRead.CONTENT_URI, cv);
            }
        });
    }
}
