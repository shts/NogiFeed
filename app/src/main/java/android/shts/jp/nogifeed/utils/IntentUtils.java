package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.content.Intent;
import android.shts.jp.nogifeed.activities.MemberDetailActivity;

public class IntentUtils {

    public static void startMemberDetailActivity(Context context) {
        Intent i = new Intent(context, MemberDetailActivity.class);
        context.startActivity(i);
    }
}
