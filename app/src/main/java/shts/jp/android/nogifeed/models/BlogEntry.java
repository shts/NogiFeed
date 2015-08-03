package shts.jp.android.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import shts.jp.android.nogifeed.utils.UrlUtils;

public class BlogEntry implements Parcelable {

    final String date;
    final String title;
    final String url;
    final String author;
    final String comment;

    public BlogEntry(String date, String title, String url, String author, String comment) {
        this.date = date;
        this.title = title;
        this.url = url;
        this.author = author;
        this.comment = comment;
    }

    public String getProfileImageUrl() {
        return UrlUtils.getImageUrlFromArticleUrl(url);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("date(").append(date).append(") ");
        sb.append("title(").append(title).append(") ");
        sb.append("url(").append(url).append(") ");
        sb.append("author(").append(author).append(") ");
        sb.append("comment(").append(comment).append(") ");
        return sb.toString();
    }

    protected BlogEntry(Parcel in) {
        date = in.readString();
        title = in.readString();
        url = in.readString();
        author = in.readString();
        comment = in.readString();
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