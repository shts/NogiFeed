package android.shts.jp.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class Entry implements Parcelable {

    public static final String KEY = Entry.class.getSimpleName();
    private static final String TAG = Entry.class.getSimpleName();

    public String title;
    public String link;
    public String id;
    public String published;
    public String updated;
    public String summary;
    public String name;
    public String content;

    public Entry() {}

    public String toString() {
        String s = "Entry : title("
                + title + ") link(" + link
                + ") id(" + id + ") published(" + published
                + ") summary(" + summary + ") name(" + name
                + ") content(" + "content"/*content*/ + ")";
        return s;
    }

    public long getPublishedDateLong() {
        try {
            return DateUtils.formatUpdatedLong(this.published);
        } catch (ParseException e) {
            Logger.e(TAG, "failed to parse date");
        }
        return -1;
    }

    public Date getPublishedDate() {
        try {
            return DateUtils.formatUpdatedDate(this.published);
        } catch (ParseException e) {
            Logger.e(TAG, "failed to parse date");
        }
        return null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(id);
        parcel.writeString(published);
        parcel.writeString(updated);
        parcel.writeString(summary);
        parcel.writeString(name);
        parcel.writeString(content);
    }

    private Entry(Parcel parcel) {
        title = parcel.readString();
        link = parcel.readString();
        id = parcel.readString();
        published = parcel.readString();
        updated = parcel.readString();
        summary = parcel.readString();
        name = parcel.readString();
        content = parcel.readString();
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel parcel) {
            return new Entry(parcel);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

}
