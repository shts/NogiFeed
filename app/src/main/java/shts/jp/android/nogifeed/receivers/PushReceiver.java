package shts.jp.android.nogifeed.receivers;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.providers.dao.UnreadArticles;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification2;

/**
 * Push receiver for FCM
 */
public class PushReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        Gson g = new Gson();
        String json = g.toJson(data);

        if (data.containsValue("object_entry")) {
            Entry e = g.fromJson(json, Entry.class);
            if (e != null) {
                BlogUpdateNotification2.showExecUiThread(this, e);
                UnreadArticles.add(this, e.getMemberId(), e.getUrl());
            }
        }
    }
}