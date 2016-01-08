package shts.jp.android.nogifeed.views.notifications;

import android.content.Context;

import shts.jp.android.nogifeed.utils.PreferencesUtils;

public abstract class NotificationWithId {

    private static final String TAG = NotificationWithId.class.getSimpleName();

    final Context context;

    public NotificationWithId(Context context) {
        this. context = context;
    }

    public int nextId() {
        return PreferencesUtils.getInt(context, getNotificationIdKey(), getNotificationIdDefVal());
    }

    public void notified(int id) {
        final int N = (getNotificationIdDefVal() + 1000) - 1;
        if (++id >= N) {
            id = getNotificationIdDefVal();
        }
        PreferencesUtils.setInt(context, getNotificationIdKey(), id);
    }

    public abstract String getNotificationIdKey();
    public abstract int getNotificationIdDefVal();

}
