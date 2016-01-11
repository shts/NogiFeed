package shts.jp.android.nogifeed.entities;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import shts.jp.android.nogifeed.common.Logger;

public class NewsList extends ArrayList<News> {

    private static final String TAG = NewsList.class.getSimpleName();

    public void filter(Context context) {
        NewsList filteredNewsList = new NewsList();
        List<News.Type> filteredTypes = News.Type.getFilteredTypes(context);
        if (filteredTypes.isEmpty()) {
            return;
        }
        for (News.Type type : filteredTypes) {
            filteredNewsList.addAll(filterNews(type));
        }
        clear();
        addAll(filteredNewsList);
    }

    private NewsList filterNews(News.Type type) {
        NewsList filteredNews = new NewsList();
        for (News news : this) {
            if (news.iconType.equals(type.iconTypeText)) {
                filteredNews.add(news);
            }
        }
        return filteredNews;
    }

    public void sort() {
        Collections.sort(this, new DateComparator(DateComparator.DESC));
    }

    private class DateComparator implements Comparator<News> {

        private static final int ASC = 1;    //昇順
        private static final int DESC = -1;    //降順
        private int sort = ASC;    //デフォルトは昇順

        private DateComparator(int sort) {
            this.sort = sort;
        }

        @Override
        public int compare(News lhs, News rhs) {
            final Date lhsDate = lhs.getDate();
            if (lhsDate == null) {
                Logger.w(TAG, "lhsDate is null");
                return 0;
            }
            final Date rhsDate = rhs.getDate();
            if (rhsDate == null) {
                Logger.w(TAG, "rhsDate is null");
                return 0;
            }
            return lhsDate.compareTo(rhsDate) * sort;
        }
    }
}
