package shts.jp.android.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import shts.jp.android.nogifeed.R;

public class News implements Parcelable {

    public static final String KEY = News.class.getSimpleName();

    public final String iconType;
    public final String date;
    public final String url;
    public final String title;

    public News(String date, String iconType, String url, String title) {
        this.iconType = iconType;
        this.date = date;
        this.url = url;
        this.title = title;
    }

    public enum Type {
        MEDIA("icon1") {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_media;
            }
        },
        EVENT("icon2") {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_event;
            }
        },
        RELEASE("icon2") {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_release;
            }
        },
        OTHER("icon4") {
            @Override
            public int getIconResource() {
                return R.drawable.ic_news_other;
            }
        },
        ;
        private final String iconTypeText;
        private Type(String iconTypeText) {
            this.iconTypeText = iconTypeText;
        }
        private String getIconTypeText() {
            return this.iconTypeText;
        }
        public static Type from(String typeText) {
            for (Type t : values()) {
                if (t.getIconTypeText().equals(typeText)) {
                    return t;
                }
            }
            return null;
        }
        public abstract int getIconResource();
    }

    public Type getNewsType() {
        return Type.from(iconType);
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