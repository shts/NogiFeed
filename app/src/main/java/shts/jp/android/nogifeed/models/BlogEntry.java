package shts.jp.android.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.utils.UrlUtils;

public class BlogEntry implements Parcelable {

    private static final String TAG = BlogEntry.class.getSimpleName();
    public static final String KEY = BlogEntry.class.getSimpleName();

    public final String date;
    public final String title;
    public final String url;
    public final String author;
    public final String comment;
    public final String content;

    public BlogEntry(String date, String title, String url, String author, String comment, String content) {
        this.date = date;
        this.title = title;
        this.url = url;
        this.author = author;
        this.comment = comment;
        this.content = content;
    }

    public Entry toEntryObject() {
        Entry e = new Entry();
        e.link = url;
        e.content = content;
        e.name = author;
        e.published = date;
        e.updated = date;
        e.title = title;
        return e;
    }

    public String getProfileImageUrl() {
        return UrlUtils.getImageUrlFromArticleUrl(url);
    }

    public String getFeedUrl() {
        return UrlUtils.getMemberFeedUrl(url);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("date(").append(date).append(") ");
        sb.append("title(").append(title).append(") ");
        sb.append("url(").append(url).append(") ");
        sb.append("author(").append(author).append(") ");
        sb.append("comment(").append(comment).append(") ");
        sb.append("content(").append(content).append(") ");
        return sb.toString();
    }

    protected BlogEntry(Parcel in) {
        date = in.readString();
        title = in.readString();
        url = in.readString();
        author = in.readString();
        comment = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(author);
        dest.writeString(comment);
        dest.writeString(content);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BlogEntry> CREATOR = new Parcelable.Creator<BlogEntry>() {
        @Override
        public BlogEntry createFromParcel(Parcel in) {
            return new BlogEntry(in);
        }

        @Override
        public BlogEntry[] newArray(int size) {
            return new BlogEntry[size];
        }
    };
}