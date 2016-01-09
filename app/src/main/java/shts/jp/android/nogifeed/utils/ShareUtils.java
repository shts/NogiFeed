package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.content.Intent;

import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.models.Entry;

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

    public static Intent getShareBlogIntent(Entry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append(entry.getTitle()).append(" | ");
        sb.append(entry.getAuthor()).append("\n");
        sb.append(entry.getBlogUrl());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        return intent;
    }
}
