package android.shts.jp.nogifeed.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.models.Member;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsoupUtils {

    private static final String TAG = JsoupUtils.class.getSimpleName();
    private static final String URL_ALL_MEMBER = "http://www.nogizaka46.com/smph/member";

    public interface GetMemberListener {
        public void onSuccess(List<Member> memberList);
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
            Logger.w(TAG, "listener is null.");
            return false;
        }

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot connection because of network disconnected.");
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Member> members = getAllMembers();
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

    private static List<Member> getAllMembers() {
        List<Member> members = new ArrayList<Member>();

        try {
            Document document = Jsoup.connect(URL_ALL_MEMBER).get();
            Element body = document.body();
            Element memberTags = body.getElementsByClass("clearfix").get(1);

            for (Element child : memberTags.children()) {
                // http://blog.nogizaka46.com/manatsu.akimoto/smph/
                Elements values = child.getElementsByAttribute("value");
                String url = null;
                for (Element value : values) {
                    url = value.getElementsByAttribute("value").first().attr("value");
                    if (TextUtils.isEmpty(url)
                            || "http://blog.nogizaka46.com/smph/".equals(url)) {
                        Logger.w(TAG, "ignore value : url(" + url + ")");
                    } else if (url.startsWith("http://blog.nogizaka46.com/")) {
                        Logger.v(TAG, "valid value : url(" + url + ")");
                        // Convert 'member blog' url to 'feed url'
                        String feedUrl = UrlUtils.getMemberFeedUrl(url);
                        Logger.v(TAG, "Convert 'member blog' url to 'feed url' : Blog url(" + url
                                + ") feed url(" + feedUrl + ")");
                        members.add(new Member(feedUrl));
                    }
                }
            }
        } catch (IOException e) {
            Logger.e(TAG, "failed to get all member urls : e(" + e + ")");
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
        Logger.i(TAG, "raw image url(" + url + ")");

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
            Logger.e(TAG, "failed to getThumbnailImageUrls() : content is null");
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
            Logger.e(TAG, "failed to getRawImagePageUrls() : maxSize(" + maxSize + ")");
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

}
