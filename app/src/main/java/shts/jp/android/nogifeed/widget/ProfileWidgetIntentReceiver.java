package shts.jp.android.nogifeed.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;

public class ProfileWidgetIntentReceiver extends BroadcastReceiver {

    private static final String TAG = ProfileWidgetIntentReceiver.class.getSimpleName();

    public static final String CLICK = "shts.jp.android.nogifeed.widget.ProfileWidgetProvider.CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.v(TAG, "in : intent(" + intent.toUri(Intent.URI_INTENT_SCHEME));
        if (CLICK.equals(intent.getAction())) {
            String memberObjectId = intent.getStringExtra(Member.KEY);
            Intent i = MemberDetailActivity.getStartIntent(context, memberObjectId);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
