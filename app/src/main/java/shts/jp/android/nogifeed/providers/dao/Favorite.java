package shts.jp.android.nogifeed.providers.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite implements Parcelable {

    public int id;
    public int memberId;

    public Favorite() {
    }

    public Favorite(int id, int memberId) {
        this.id = id;
        this.memberId = memberId;
    }

    protected Favorite(Parcel in) {
        id = in.readInt();
        memberId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(memberId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Favorite> CREATOR = new Parcelable.Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id(").append(id).append(")");
        sb.append("memberId(").append(memberId).append(")");
        return sb.toString();
    }
}
