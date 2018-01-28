package shts.jp.android.nogifeed.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.entities.NewsList;

public class AsyncNewsClient {

    private static final String TAG = AsyncNewsClient.class.getSimpleName();
    private static final String URL_NEWS = "http://www.nogizaka46.com/smph/news/";

    public static Observable<NewsList> get(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<NewsList>() {
            @Override
            public void call(Subscriber<? super NewsList> subscriber) {
                try {
                    NewsList newsList = getNewsFeed(URL_NEWS);
                    newsList.filter(context);
                    newsList.sort();
                    subscriber.onNext(newsList);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
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
