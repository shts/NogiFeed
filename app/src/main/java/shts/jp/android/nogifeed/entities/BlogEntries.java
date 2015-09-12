package shts.jp.android.nogifeed.entities;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by saitoushouta on 15/07/28.
 */
public class BlogEntries extends ArrayList<BlogEntry> {

    public BlogEntry getEntryFrom(String article) {
        for (BlogEntry e : this) {
            if (TextUtils.isEmpty(e.url)) {
                continue;
            }
            if (e.url.equals(article)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BlogEntries : [");
        for (BlogEntry e : this) {
            if (e == null) {
                continue;
            }
            sb.append(e.toString()).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
