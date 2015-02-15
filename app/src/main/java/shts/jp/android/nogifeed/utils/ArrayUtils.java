package shts.jp.android.nogifeed.utils;

import java.util.List;

public class ArrayUtils {

    public synchronized static void concatenation(List in, List out) {
        for (Object o : in) {
            out.add(o);
        }
    }
}
