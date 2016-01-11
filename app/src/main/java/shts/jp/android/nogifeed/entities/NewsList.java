package shts.jp.android.nogifeed.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NewsList extends ArrayList<News> {

    private static final String TAG = NewsList.class.getSimpleName();

    public void sort() {
        Collections.sort(this, new DateComparator(DateComparator.DESC));
    }

    private class DateComparator implements Comparator {

        private static final int ASC = 1;    //昇順
        private static final int DESC = -1;    //降順
        private int sort = ASC;    //デフォルトは昇順

        private DateComparator(int sort) {
            this.sort = sort;
        }

        @Override
        public int compare(Object lhs, Object rhs) {
            return 0;
        }
    }
}
