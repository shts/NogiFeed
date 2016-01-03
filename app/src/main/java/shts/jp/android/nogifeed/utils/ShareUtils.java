package shts.jp.android.nogifeed.utils;

import android.content.Intent;

import shts.jp.android.nogifeed.entities.News;

public class ShareUtils {

    private static final String TAG = ShareUtils.class.getSimpleName();

    public static Intent getNewsFeedIntent(News news) {
        StringBuilder sb = new StringBuilder();
        sb.append(news.title).append("\n");
        sb.append(news.url);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        return intent;
    }
}
