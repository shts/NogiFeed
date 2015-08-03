package shts.jp.android.nogifeed.api;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.BlogEntry;
import shts.jp.android.nogifeed.utils.NetworkUtils;

// TODO: implement pagination
public class AsyncBlogFeedClient {

    private static final String TAG = AsyncBlogFeedClient.class.getSimpleName();
    private static final String URL = "http://blog.nogizaka46.com/";
    private static final String USER_AGENT
            = "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52";

    private static final HandlerThread WORKER_THREAD = new HandlerThread("AsyncBlogFeedClient");
    private static Handler HANDLER;
    static {
        WORKER_THREAD.start();
        HANDLER = new Handler(WORKER_THREAD.getLooper());
    }

    public interface Callbacks {
        public void onFinish(ArrayList<BlogEntry> blogEntries);
    }

    public static class Target {
        public int year;
        public int month;
        public Page page;
        public Target(int from, int to) {
            Calendar calendar = Calendar.getInstance();
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH) + 1;
            this.page = new Page(from, to);
        }
        public Target(int year, int month, int from, int to) {
            this.year = year;
            this.month = month;
            this.page = new Page(from, to);
        }
        static class Page {
            public int from;
            public int to;
            Page(int from, int to) {
                this.from = from;
                this.to = to;
            }
        }

        public void nextPage() {
            Logger.v(TAG, "next before(" + toString() + ")");
//            page.from += 8;
//            page.to += 8;
            Logger.v(TAG, "next after(" + toString() + ")");
        }

        void nextMonth() {
            Logger.v(TAG, "next before(" + toString() + ")");
            if (month == 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            page.from = 1;
            page.to = 8;
            Logger.v(TAG, "next after(" + toString() + ")");
        }

        String getDateParameter() {
            return String.valueOf(year) + (month < 10 ? String.valueOf("0") + month : month);
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("year(").append(year).append(") ");
            sb.append("month(").append(month).append(") ");
            sb.append("from(").append(page.from).append(") ");
            sb.append("to(").append(page.to).append(") ");
            return sb.toString();
        }
    }

    public static boolean getBlogEntry(final Context context,
                                       final Target target,
                                       final Callbacks callbacks) {
        if (callbacks == null) {
            Logger.w(TAG, "cannot connection because of callback is null.");
            return false;
        }

        if (target == null) {
            Logger.w(TAG, "cannot connection because of entryTarget is null.");
            return false;
        }

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot connection because of network disconnected.");
            return false;
        }

        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<BlogEntry> blogEntries
                            = getBlogEntry(target);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callbacks.onFinish(blogEntries);
                        }
                    });

                } catch (IOException e) {
                    Logger.e(TAG, "failed to parse blog feed", e);
                }
            }
        });

        return true;
    }

    private static ArrayList<BlogEntry> getBlogEntry(
            final Target target) throws IOException {

        final ArrayList<BlogEntry> blogEntries = new ArrayList<BlogEntry>();

        // decriment read page to max page size
        final int maxSize = getPaginate(target.getDateParameter());
        Logger.v(TAG, "getBlogEntry(Target) in : maxSize(" + maxSize
                + ") to(" + target.page.to + ")");
        final boolean needToNextMonth = (maxSize < (target.page.to + 1));
        while (maxSize < (target.page.to + 1)) {
            target.page.to--;
        }
        Logger.v(TAG, "getBlogEntry(Target) in : target(" + target.toString() + ")");

        for (int i = target.page.from; i < (target.page.to + 1); i++) {

            String pageUrl = URL + "?p=" + i + "&d=" + target.getDateParameter();
            Logger.d(TAG, "getBlogEntry() : pageUrl(" + pageUrl + ")");

            Document document = Jsoup.connect(pageUrl).userAgent(USER_AGENT).get();

            Element body = document.body();
            Element sheet = body.getElementById("sheet");
            Elements clearfix = sheet.getElementsByClass("clearfix");

            final int size = clearfix.size();
            for (int j = 0; j < size; j++) {
                Element e = clearfix.get(j);

                String yearmonth = e.getElementsByClass("yearmonth").get(0).text();
                String dd1 = e.getElementsByClass("dd1").get(0).text();
                String dd2 = e.getElementsByClass("dd2").get(0).text();
                String date = yearmonth + "/" + dd1 + " " + dd2;

                String title = e.getElementsByClass("entrytitle").get(0).text();
                String author = e.getElementsByClass("author").get(0).text();
                String url = e.getElementsByTag("a").get(0).attr("href");

                String comment = body.getElementsByClass("entrybottom")
                        .get(j).getElementsByTag("a").get(1)
                        .text().replace("コメント(", "").replace(")", "");

                blogEntries.add(new BlogEntry(date, title, url, author, comment));
            }
        }

        if (needToNextMonth) {
            target.nextMonth();
        }

        return blogEntries;
    }

    private static int getPaginate(String param) throws IOException {
        Document document = Jsoup.connect(URL + "?d=" + param).userAgent(USER_AGENT).get();
        Element paginate = document.body()
                .getElementsByClass("paginate").get(0);
        return paginate.getElementsByTag("a").size();
    }
}
