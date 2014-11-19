package android.shts.jp.nogifeed.utils;

import android.os.Handler;
import android.os.Looper;
import android.shts.jp.nogifeed.models.Member;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsoupUtils {

    private static final String TAG = JsoupUtils.class.getSimpleName();
    private static final String URL_ALL_MEMBER = "http://www.nogizaka46.com/smph/member";
    private static final String MEMBER_PROFILE_URL_SCHEME = "http://www.nogizaka46.com";

    public interface JsoupListener {
        public void onSuccess(List<Member> memberList);
        public void onFailed();
    }

    private static Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void getAllMembers(final JsoupListener listener) {
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
    }

    private synchronized static List<Member> getAllMembers() {

        List<Member> memberList = new ArrayList<Member>();

        try {
            Document document = Jsoup.connect(URL_ALL_MEMBER).get();
            Element body = document.body();
            Element members = body.getElementsByClass("clearfix").get(2);

            for (Element child : members.children()) {
                // ./detail/wadamaaya.php
                String feedUrl = URL_ALL_MEMBER
                        + child.getElementsByAttribute("href").first().attr("href").substring(1/* ignore dot */);
                String profileImageUrl = MEMBER_PROFILE_URL_SCHEME
                        + child.getElementsByAttribute("src").first().attr("src");
                String name = child.getElementsByAttribute("alt").first().attr("alt");

                memberList.add(new Member(feedUrl, profileImageUrl, name));
            }
        } catch (IOException e) {
            Log.e(TAG, "failed to get all member urls");
        }
        return memberList;
    }
}
