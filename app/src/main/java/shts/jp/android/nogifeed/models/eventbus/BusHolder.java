package shts.jp.android.nogifeed.models.eventbus;

import com.squareup.otto.Bus;

public class BusHolder {

    private static final String TAG = BusHolder.class.getSimpleName();

    private static final Bus BUS = new Bus();

    public static Bus get() {
        return BUS;
    }

}
