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

    public interface JsoupListener {
        public void onSuccess(List<Member> memberList);
        public void onFailed();
    }

    private static Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * Get all member's objects from 'http://www.nogizaka46.com/smph/member'.
     * @param listener callbacks when get all member's objects.
     */
    public static boolean getAllMembers(Context context, final JsoupListener listener) {

        if (!NetworkUtils.isConnected(context)
                || NetworkUtils.isAirplaneModeOn(context)) {
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
                            if (listener != null) {
                                listener.onSuccess(members);
                            }
                        }
                    });
                } else {
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onFailed();
                            }
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
            Logger.e(TAG, "failed to get all member urls");
        }
        return members;
    }
}
