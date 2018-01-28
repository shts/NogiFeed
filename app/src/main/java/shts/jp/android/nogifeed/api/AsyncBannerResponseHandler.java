package shts.jp.android.nogifeed.api;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Xml;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import shts.jp.android.nogifeed.entities.Banner;

public abstract class AsyncBannerResponseHandler extends AsyncHttpResponseHandler {

    private static final String TAG = AsyncBannerResponseHandler.class.getSimpleName();

    private static final HandlerThread WORKER_THREAD = new HandlerThread("AsyncBannerResponseHandler");
    private static Handler HANDLER;
    static {
        WORKER_THREAD.start();
        HANDLER = new Handler(WORKER_THREAD.getLooper());
    }
    private static Handler UI_THREAD = new Handler(Looper.getMainLooper());

    public AsyncBannerResponseHandler() {
        super(HANDLER.getLooper());
    }

    public abstract void onFinish(ArrayList<Banner> bannerList);

    // int statusCode, Header[] headers, byte[] responseBytes
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        ArrayList<Banner> banners = Parser.parse(responseBody);
        finish(banners);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        finish(null);
    }

    private void finish(final ArrayList<Banner> bannerList) {
        UI_THREAD.post(new Runnable() {
            @Override
            public void run() {
                onFinish(bannerList);
            }
        });
    }

    static class Parser {
        private static final String array_item = "array_item";
        private static final String idxbnr = "idxbnr";
        private static final String alttext = "alttext";
        private static final String thumurl = "thumurl";
        private static final String bnrurl = "bnrurl";
        private static final String linkurl = "linkurl";

        private static final String CHARSET_NAME = "UTF-8";

        static ArrayList<Banner> parse(byte[] responseBody) {
            try {
                return parse(new ByteArrayInputStream(responseBody));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        private static ArrayList<Banner> parse(InputStream data) throws XmlPullParserException, IOException {
            ArrayList<Banner> bannerList = new ArrayList<Banner>();
            Banner banner = null;

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(data, CHARSET_NAME);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (idxbnr.equals(tag)) {

                        } else if (array_item.equals(tag)) {
                            banner = new Banner();

                        } else if (alttext.equals(tag)) {
                            String alttext = parser.nextText();
                            banner.alttext = alttext;

                        } else if (thumurl.equals(tag)) {
                            String thumurl = parser.nextText();
                            banner.thumurl = thumurl;

                        } else if (bnrurl.equals(tag)) {
                            String bnrurl = parser.nextText();
                            banner.bnrurl = bnrurl;

                        } else if (linkurl.equals(tag)) {
                            String linkurl = parser.nextText();
                            banner.linkurl = linkurl;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (array_item.equals(tag)) {
                            bannerList.add(banner);
                            banner = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            return bannerList;
        }
    }


}
