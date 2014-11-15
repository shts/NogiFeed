package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.shts.jp.nogifeed.activities.BlogActivity;
import android.shts.jp.nogifeed.activities.MemberDetailActivity;
import android.shts.jp.nogifeed.models.Entry;

import org.apache.http.protocol.HTTP;

public class IntentUtils {

    private static final String URL_TWITTER = "https://twitter.com/";
    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=shts.jp.android.nogifeed&hl=ja";
    private static final String RECOMEND_TEXT = "乃木坂46公式ブログは NogiFeed で読みましょう！";

    public static void showDeveloper(Context context) {
        Uri uri = Uri.parse(URL_TWITTER + "shts_dev");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void inquiryApp(Context context) {
        Uri uri = Uri.parse(URL_TWITTER + "nogifeed");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse(PLAY_STORE_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void recomendApp(Context context) {
        String text = RECOMEND_TEXT + "\n" + PLAY_STORE_URL;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(HTTP.PLAIN_TEXT_TYPE);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(intent);
    }

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
