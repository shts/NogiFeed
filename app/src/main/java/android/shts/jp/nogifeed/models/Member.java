package android.shts.jp.nogifeed.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.shts.jp.nogifeed.common.Logger;
import android.shts.jp.nogifeed.utils.DataStoreUtils;
import android.shts.jp.nogifeed.utils.StringUtils;
import android.shts.jp.nogifeed.utils.UrlUtils;

public class Member implements Parcelable {

    public static final String KEY = Member.class.getSimpleName();
    private static final String TAG = Member.class.getSimpleName();

    //public String blogUrl; // http://www.nogizaka46.com/smph/member/detail/kitanohinako.php
    public String allArticleUrl; // http://blog.nogizaka46.com/manatsu.akimoto/smph/
    public final String feedUrl;
    public final String profileImageUrl; // http://img.nogizaka46.com/www/smph/member/img/akimotomanatsu_prof.jpg
    public String fullName; /* kanji */

    public Member(String allArticleUrl) {
        this.allArticleUrl = allArticleUrl;
        this.feedUrl = UrlUtils.getMemberFeedUrl(allArticleUrl);
        this.profileImageUrl = UrlUtils.getImageUrlFromArticleUrl(allArticleUrl);
        Logger.v(TAG, "create Member object. " + toString());
    }

    public String toString() {
        return " feedUrl("
                + feedUrl + ") profileImageUrl("
                + profileImageUrl + ") allArticleUrl("
                + allArticleUrl + ")";
    }

    private Member(Parcel parcel) {
        feedUrl = parcel.readString();
        profileImageUrl = parcel.readString();
        fullName = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(feedUrl);
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
        return DataStoreUtils.alreadyExist(context, feedUrl);
    }

}
