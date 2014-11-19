package android.shts.jp.nogifeed.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.shts.jp.nogifeed.utils.DataStoreUtils;
import android.util.Log;

import java.util.List;

public class Member implements Parcelable {

    public static final String KEY = Member.class.getSimpleName();
    private static final String TAG = Member.class.getSimpleName();

    public String blogUrl; // http://www.nogizaka46.com/smph/member/detail/kitanohinako.php
    public String profileImageUrl;
    public String fullName; /* kanji */

    public Member(String feedUrl, String profileImageUrl, String fullName) {
        Log.v(TAG, "create Member object. " + toString());
        this.blogUrl = feedUrl;
        this.profileImageUrl = profileImageUrl;
        this.fullName = fullName;
    }

    public String toString() {
        return " blogUrl("
                + blogUrl + ") profileImageUrl("
                + profileImageUrl + ") fullName("
                + fullName + ")";
    }

    private Member(Parcel parcel) {
        blogUrl = parcel.readString();
        profileImageUrl = parcel.readString();
        fullName = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(blogUrl);
        parcel.writeString(profileImageUrl);
        parcel.writeString(fullName);
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

    public boolean isFavorite(Context context) {
        return DataStoreUtils.alreadyExist(context, blogUrl);
    }

}
