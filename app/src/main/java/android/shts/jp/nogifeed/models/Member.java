package android.shts.jp.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Member implements Parcelable {

    public static final String KEY = Member.class.getSimpleName();
    private static final String TAG = Member.class.getSimpleName();

    public String firstName;
    public String lastName;
    public Details details;
    public List<String> tags;

    public Member(String firstName, String lastName, Details details, List<String> tags) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.details = details;
        this.tags = tags;
    }

    private Member(Parcel parcel) {
        firstName = parcel.readString();
        lastName = parcel.readString();
        details = parcel.readParcelable(Details.class.getClassLoader());
        parcel.readStringList(tags);
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeParcelable(details, 1);
        parcel.writeStringList(tags);
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel parcel) {
            return new Member(parcel);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

}
