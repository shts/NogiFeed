package shts.jp.android.nogifeed.providers.dao;

import android.os.Parcel;
import android.os.Parcelable;


public class UnreadArticle implements Parcelable {

    private int id;
    private int memberId;
    private String url;

    UnreadArticle() {
    }

    UnreadArticle(int id, int memberId, String url) {
        this.id = id;
        this.memberId = memberId;
        this.url = url;
    }

    protected UnreadArticle(Parcel in) {
        id = in.readInt();
        memberId = in.readInt();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(memberId);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UnreadArticle> CREATOR = new Parcelable.Creator<UnreadArticle>() {
        @Override
        public UnreadArticle createFromParcel(Parcel in) {
            return new UnreadArticle(in);
        }

        @Override
        public UnreadArticle[] newArray(int size) {
            return new UnreadArticle[size];
        }
    };

}
