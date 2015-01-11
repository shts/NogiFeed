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
                    url = value.getElementsByAttribute("value").first().attr("value").toString();
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
        try {
            Document document = Jsoup.parse(html);
            Element body = document.body();
            Element contents = body.getElementById("contents");
            Element imgTags = contents.getElementsByTag("img").get(0);
            String url = StringUtils.ignoreHtmlTags(imgTags.toString());
            Logger.i(TAG, "raw image url(" + url + ")");

            if (!TextUtils.isEmpty(url)
                    && StringUtils.isValidDomain(url)) {
                return url;
            }
            return null;

        } catch (Exception e) {
            Logger.e(TAG, "failed to get enable raw image url : e(" + e + ")");
        }
        return null;
    }

}
