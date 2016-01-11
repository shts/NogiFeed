package shts.jp.android.nogifeed.entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.utils.PreferencesUtils;

public class News implements Parcelable {

    public static final String TAG = News.class.getSimpleName();

    public final String iconType;
    public final String date; // 2016.01.07
    public final String url;
    public final String title;

    public News(String date, String iconType, String url, String title) {
        this.iconType = iconType;
        this.date = date;
        this.url = url;
        this.title = title;
    }

    public News(String date, int category, String url, String title) {
        this.iconType = Type.from(category).iconTypeText;
        this.date = date;
        this.url = url;
        this.title = title;
    }

    public enum Type {
        MEDIA("icon1", 1) {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_media;
            }

            @Override
            public String getNewsUrl() {
                return "http://www.nogizaka46.com/smph/news/media/";
            }
        },
        EVENT("icon2", 2) {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_event;
            }

            @Override
            public String getNewsUrl() {
                return "http://www.nogizaka46.com/smph/news/events/";
            }
        },
        RELEASE("icon3", 3) {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_release;
            }

            @Override
            public String getNewsUrl() {
                return "http://www.nogizaka46.com/smph/news/releases/";
            }
        },
        OTHER("icon4", 4) {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_other;
            }

            @Override
            public String getNewsUrl() {
                return "http://www.nogizaka46.com/smph/news/etc/";
            }
        },
        ;
        private static final String URL_NEWS = "http://www.nogizaka46.com/smph/news/";
        public final String iconTypeText;
        private final int iconTypeNumber;
        private Type(String iconTypeText, int iconTypeNumber) {
            // Feedで利用
            this.iconTypeText = iconTypeText;
            // Notificationで利用
            this.iconTypeNumber = iconTypeNumber;
        }
        public static Type from(String typeText) {
            for (Type t : values()) {
                if (t.iconTypeText.equals(typeText)) {
                    return t;
                }
            }
            return null;
        }
        public static Type from(int typeText) {
            for (Type t : values()) {
                if (t.iconTypeNumber == typeText) {
                    return t;
                }
            }
            return null;
        }
        public static String[] getTypeList(Context context) {
            return context.getResources().getStringArray(R.array.news_category_array);
        }

        public static List<Type> getFilteredTypes(Context context) {
            List<Type> filteredTypes = new ArrayList<>();
            final boolean[] filter = getFilter(context);
            for (int i = 0; i < filter.length; i++) {
                if (filter[i]) {
                    filteredTypes.add(values()[i]);
                }
            }
            return filteredTypes;
        }

        public static void setFilter(Context context, boolean[] filter) {
            final int N = values().length;
            for (int i = 0; i < N; i++) {
                final String key = "pref_key_filter_" + values()[i].name();
                PreferencesUtils.setBoolean(context, key, filter[i]);
            }
        }
        public static boolean[] getFilter(Context context) {
            boolean[] filter = new boolean[Type.values().length];
            final int N = values().length;
            for (int i = 0; i < N; i++) {
                final String key = "pref_key_filter_" + values()[i].name();
                filter[i] = PreferencesUtils.getBoolean(context, key, true);
            }
            return filter;
        }
        public abstract int getIconResource();
        public abstract String getNewsUrl();
    }

    public Type getNewsType() {
        return Type.from(iconType);
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd");
    public Date getDate() {
        try {
            return SDF.parse(this.date);
        } catch (ParseException e) {
            Logger.e(TAG, "failed to parse date", e);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("date(").append(date).append(") ");
        sb.append("iconType(").append(iconType).append(") ");
        sb.append("url(").append(url).append(") ");
        sb.append("title(").append(title).append(") ");
        return sb.toString();
    }

    protected News(Parcel in) {
        iconType = in.readString();
        date = in.readString();
        url = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iconType);
        dest.writeString(date);
        dest.writeString(url);
        dest.writeString(title);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}