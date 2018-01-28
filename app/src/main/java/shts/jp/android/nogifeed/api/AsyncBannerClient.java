package shts.jp.android.nogifeed.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

public class AsyncBannerClient {

    private static final String URL = "http://www.nogizaka46.com/xml/topbannerdata.xml";

    private static AsyncHttpClient sClient = new AsyncHttpClient();

    public static boolean getBannerList(final Context context,
                                        final AsyncBannerResponseHandler responseHandler) {
        if (responseHandler == null) {
            return false;
        }
        sClient.get(URL, responseHandler);
        return true;
    }
}
