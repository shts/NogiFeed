package shts.jp.android.nogifeed.api;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.entities.NewsList;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.utils.NetworkUtils;

public class AsyncNewsClient {

    private static final String TAG = AsyncNewsClient.class.getSimpleName();
    private static final String URL_NEWS = "http://www.nogizaka46.com/smph/news/";

    public static class GetNewsFeedCallback {
        public final List<News> newsList;
        public GetNewsFeedCallback(List<News> newsList) {
            this.newsList = newsList;
        }
        public boolean hasError() {
            return this.newsList == null || this.newsList.isEmpty();
        }
    }

    public static boolean get(final Context context) {
        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot connection because of network disconnected.");
            return false;
        }
        getAsyncNewsFeed(context);
        return true;
    }

    private static void getAsyncNewsFeed(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NewsList newsList = getNewsFeed(URL_NEWS);
                    newsList.filter(context);
                    newsList.sort();
                    callback(newsList);
                } catch (IOException e) {
                    callback(null);
                }
            }
        }).start();
    }

    private static void callback(final List<News> newsList) {
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                BusHolder.get().post(new GetNewsFeedCallback(newsList));
            }
        });
    }

    private static NewsList getNewsFeed(final String url) throws IOException {
        final NewsList newsList = new NewsList();

        Document document = Jsoup.connect(url).get();
        Element body = document.body();
        Element dl = body.getElementsByTag("dl").get(0);

        final int length = dl.getElementsByTag("dt").size();
        for (int i = 0; i < length; i++) {
            String iconTypeText = dl.getElementsByTag("dt").get(i).className();
            if (TextUtils.isEmpty(iconTypeText)) {
                iconTypeText = "icon4";
            }
            final String date = dl.getElementsByTag("dt").get(i).text();
            final String title = dl.getElementsByTag("dd").get(i).text();
            final String newsUrl = dl.getElementsByTag("a").get(i).attr("href");
            newsList.add(new News(date, iconTypeText, newsUrl, title));
        }
        return newsList;
    }
}
