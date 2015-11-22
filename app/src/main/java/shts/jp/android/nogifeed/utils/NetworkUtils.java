package shts.jp.android.nogifeed.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import shts.jp.android.nogifeed.common.Logger;

public class NetworkUtils {

    private NetworkUtils() {}

    /**
     * Check network enable.
     * @param context application context.
     * @return if true network enabled.
     */
    public static boolean enableNetwork(Context context) {
        return NetworkUtils.isConnected(context);
    }

    /**
     * Whether be able to network.
     *
     * @param context
     * @return Return true if network is enable.
     */
    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if( ni != null ){
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

}
