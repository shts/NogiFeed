package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.News;

public class JsoupUtils {

    private static final String TAG = JsoupUtils.class.getSimpleName();
    private static final String URL_ALL_MEMBER = "http://www.nogizaka46.com/smph/member";
    private static final String URL_NEWS = "http://www.nogizaka46.com/smph/news/";

    public interface GetMemberListener {
        public void onSuccess(List<shts.jp.android.nogifeed.models.Member> memberList);
        public void onFailed();
    }

    public interface GetNewsFeedListener {
        public void onSuccess(List<News> newsList);
        public void onFailed();
    }

    private static Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * Get all member's objects from 'http://www.nogizaka46.com/smph/member'.
     * @param context application context.
     * @param listener callbacks when get all member's objects.
     * @return did execute.
     */
    public static boolean getAllMembers(Context context, final GetMemberListener listener) {

        if (listener == null) {
            shts.jp.android.nogifeed.common.Logger.w(TAG, "listener is null.");
            return false;
        }

        if (!shts.jp.android.nogifeed.utils.NetworkUtils.enableNetwork(context)) {
            shts.jp.android.nogifeed.common.Logger.w(TAG, "cannot connection because of network disconnected.");
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<shts.jp.android.nogifeed.models.Member> members = getAllMembers();
                if (members != null && !members.isEmpty()) {
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(members);
                        }
                    });
                } else {
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed();
                        }
                    });
                }
            }
        }).start();

        return true;
    }

    private static List<shts.jp.android.nogifeed.models.Member> getAllMembers() {
        List<shts.jp.android.nogifeed.models.Member> members = new ArrayList<shts.jp.android.nogifeed.models.Member>();

        try {
            Document document = Jsoup.connect(URL_ALL_MEMBER).get();
            Element body = document.body();
            Element memberTags = body.getElementsByClass("clearfix").get(1);
            Elements options = memberTags.getElementsByTag("option");

            for (Element option : options) {
                String url = option.attr("value");
                String name = option.text();
                Log.i(TAG, "elements : url(" + url + ") name(" + name + ")");

                if (TextUtils.isEmpty(url)
                        || "http://blog.nogizaka46.com/smph/".equals(url)) {
                    shts.jp.android.nogifeed.common.Logger.w(TAG, "ignore value : url(" + url + ")");

                } else if (url.startsWith("http://blog.nogizaka46.com/")) {
                    shts.jp.android.nogifeed.common.Logger.v(TAG, "valid value : url(" + url
                            + ") name(" + name + ")");
                    members.add(new shts.jp.android.nogifeed.models.Member(url, name));
                }
            }
        } catch (IOException e) {
            shts.jp.android.nogifeed.common.Logger.e(TAG, "failed to get all member urls : e(" + e + ")");
        }
        return members;
    }

    /**
     * Get enable raw image url from blog article.
     * @param html blog article html.
     * @return enable raw image url if url enable.
     */
    public static String getEnableRawImageUrl(String html) {
        Document document = Jsoup.parse(html);
        Element body = document.body();
        Element contents = body.getElementById("contents");
        Element imgTags = contents.getElementsByTag("img").get(0);
        String url = imgTags.attr("src");
        shts.jp.android.nogifeed.common.Logger.i(TAG, "raw image url(" + url + ")");

        if (!TextUtils.isEmpty(url) && isValidDomain(url)) {
            return url;
        }
        return null;
    }

    /**
     * Get thumbnail image urls from blog content. Extract url from HTML tag <img src...>.
     * @param content blog content.
     * @param maxSize numbers of url extracted. if 0 then unlimited.
     * @return thumbnail image urls
     */
    public static List<String> getThumbnailImageUrls(String content, int maxSize) {

        if (TextUtils.isEmpty(content)) {
            shts.jp.android.nogifeed.common.Logger.e(TAG, "failed to getThumbnailImageUrls() : content is null");
            return null;
        }

        if (maxSize < 0) {
            Logger.e(TAG, "failed to getThumbnailImageUrls() : maxSize(" + maxSize + ")");
            return null;
        }

        List<String> thumbnailUrls = new ArrayList<String>();

        Document document = Jsoup.parse(content);
        Element body = document.body();
        Elements imgTags = body.getElementsByTag("img");

        for (Element e : imgTags) {
            if (maxSize != 0) {
                if (thumbnailUrls.size() >= maxSize) break;
            }
            String url = e.attr("src");
            if (!TextUtils.isEmpty(url) && !url.contains(".gif")) {
                thumbnailUrls.add(url);
            }
        }
        return thumbnailUrls;
    }

    /**
     * Get raw image urls from blog content. Extract url from HTML tag <a href...>.
     * @param content blog content.
     * @param maxSize numbers of url extracted. if 0 then unlimited.
     * @return raw image urls
     */
    public static List<String> getRawImagePageUrls(String content, int maxSize) {

        if (TextUtils.isEmpty(content)) {
            Logger.e(TAG, "failed to getRawImagePageUrls() : content is null");
            return null;
        }

        if (maxSize < 0) {
            shts.jp.android.nogifeed.common.Logger.e(TAG, "failed to getRawImagePageUrls() : maxSize(" + maxSize + ")");
            return null;
        }

        List<String> imagePageUrls = new ArrayList<String>();

        Document document = Jsoup.parse(content);
        Element body = document.body();
        Elements imgTags = body.getElementsByTag("a");

        for (Element e : imgTags) {
            if (maxSize != 0) {
                if (imagePageUrls.size() >= maxSize) break;
            }
            String url = e.attr("href");
            if (!TextUtils.isEmpty(url) && isValidDomain(url)) {
                imagePageUrls.add(url);
            }
        }
        return imagePageUrls;
    }

    private static boolean isValidDomain(String url) {
        return url.contains("http://dcimg.awalker.jp");
    }

    /**
     * get thumbnail index at blog
     * need replace.
     * [get url] -> 'http://blog.nogizaka46.com/'
     * [target url] -> 'http://img.nogizaka46.com/blog/'
     * @param content html
     * @param targetUrl download target url
     * @return index of target url at blog entry
     */
    public static int getThumbnailIndex(String content, String targetUrl) {
        List<String> urls = getThumbnailImageUrls(content, 0);
        if (urls == null || urls.isEmpty()) {
            Logger.d(TAG, "getThumbnailIndex urls is null or empty");
            return -1;
        }

        targetUrl = targetUrl.replace("http://img.nogizaka46.com/blog/", "http://blog.nogizaka46.com/");
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            if (url.equals(targetUrl)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean getNewsFeed(final Context context, final News.Type newsType,
                                      final GetNewsFeedListener listener) {
        if (listener == null) {
            Logger.w(TAG, "listener is null.");
            return false;
        }

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot connection because of network disconnected.");
            return false;
        }

        String newsFeedUrl = null;
        if (newsType == null) {
            newsFeedUrl = URL_NEWS;
        } else {
            newsFeedUrl = newsType.getNewsUrl();
        }

        return getNewsFeed(newsFeedUrl, listener);
    }

    public static boolean getNewsFeed(final String url,
                                      final GetNewsFeedListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<News> newsList = getNewsFeed(url);
                    if (newsList != null && !newsList.isEmpty()) {
                        HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(newsList);
                            }
                        });
                    } else {
                        HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailed();
                            }
                        });
                    }
                } catch (IOException e) {
                    listener.onFailed();
                }
            }
        }).start();

        return true;
    }

    public static ArrayList<News> getNewsFeed(final String url) throws IOException {
        final ArrayList<News> newsList = new ArrayList<News>();

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
