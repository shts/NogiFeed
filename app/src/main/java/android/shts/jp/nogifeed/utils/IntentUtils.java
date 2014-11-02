package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.content.Intent;
import android.shts.jp.nogifeed.activities.BlogActivity;
import android.shts.jp.nogifeed.activities.MemberDetailActivity;
import android.shts.jp.nogifeed.models.Entry;

public class IntentUtils {

    public static void startMemberDetailActivity(Context context, Entry entry) {
        Intent i = new Intent(context, MemberDetailActivity.class);
        i.putExtra(Entry.KEY, entry);
        context.startActivity(i);
    }

    public static void startBlogActivity(Context context, Entry entry) {
        Intent i = new Intent(context, BlogActivity.class);
        i.putExtra(Entry.KEY, entry);
        context.startActivity(i);
    }
}
