package shts.jp.android.nogifeed.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Entries extends ArrayList<shts.jp.android.nogifeed.models.Entry> {

    public Entries() {}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entries : [");
        for (Entry e : this) {
            sb.append(e.toString()).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public synchronized Entries cat(Entries entries) {
        shts.jp.android.nogifeed.utils.ArrayUtils.concatenation(entries, this);
        return this;
    }

    public synchronized Entries sort() {
        Collections.sort(this, new DateComparator(DateComparator.DESC));
        return this;
    }

    private static class DateComparator implements Comparator {

        public static final int ASC = 1;    //昇順
        public static final int DESC = -1;    //降順
        private int sort = ASC;    //デフォルトは昇順

        public DateComparator(int sort) {
            this.sort = sort;
        }

        @Override
        public int compare(Object arg0, Object arg1) {
            Entry entry0 = (Entry) arg0;
            Entry entry1 = (Entry) arg1;

            return entry0.getPublishedDate().compareTo(entry1.getPublishedDate()) * sort;
        }
    }

}
