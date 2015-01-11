package android.shts.jp.nogifeed.common;

import android.content.Context;

/**
 * for debug functions
 */
public class Debug {

    public static boolean isCurrent(final Context context) {
        return Thread.currentThread().equals(context.getMainLooper().getThread());
    }

}
