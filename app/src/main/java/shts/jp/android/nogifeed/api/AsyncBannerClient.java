package shts.jp.android.nogifeed.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.utils.NetworkUtils;

public class AsyncBannerClient {

    private static final String TAG = AsyncBannerClient.class.getSimpleName();
    private static final String URL = "http://www.nogizaka46.com/xml/topbannerdata.xml";

    private static AsyncHttpClient sClient = new AsyncHttpClient();

    public static boolean getBannerList(
            final Context context, final AsyncBannerResponseHandler responseHandler) {

        if (responseHandler == null) {
            Logger.w(TAG, "cannot connection because of callback is null.");
            return false;
        }

        if (!NetworkUtils.enableNetwork(context)) {
            Logger.w(TAG, "cannot download because of network disconnected.");
            return false;
        }

        sClient.get(URL, responseHandler);

        return true;
    }

}
