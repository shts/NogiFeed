package shts.jp.android.nogifeed.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.providers.NogiFeedContent;
import shts.jp.android.nogifeed.utils.UrlUtils;

public class Member implements Parcelable {

    public static final String KEY = Member.class.getSimpleName();
    private static final String TAG = Member.class.getSimpleName();

    //public String blogUrl; // http://www.nogizaka46.com/smph/member/detail/kitanohinako.php
    public final String allArticleUrl; // http://blog.nogizaka46.com/manatsu.akimoto/smph/
    public final String feedUrl; // http://blog.nogizaka46.com/manatsu.akimoto/atom.xml
    public final String profileImageUrl; // http://img.nogizaka46.com/www/smph/member/img/akimotomanatsu_prof.jpg
    public final String name; /* kanji */

    public Member(String allArticleUrl, String name) {
        if (!TextUtils.isEmpty(allArticleUrl)
                && allArticleUrl.contains(".xml")) {
            // all member feed
            this.name = name;
            this.allArticleUrl = null;
            this.feedUrl = allArticleUrl;
            this.profileImageUrl = null;
        } else {
            this.name = name;
            this.allArticleUrl = allArticleUrl;
            this.feedUrl = UrlUtils.getMemberFeedUrl(allArticleUrl);
            this.profileImageUrl = UrlUtils.getImageUrlFromArticleUrl(allArticleUrl);
        }
    }

    public Member(Cursor c) {
        this.name = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.KEY_NAME));
        this.allArticleUrl = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.KEY_ARTICLE_URL));
        this.feedUrl = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.KEY_FEED_URL));
        this.profileImageUrl = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.ProfileWidget.KEY_IMAGE_URL));
    }

    public String toString() {
        return "name(" + name
                + ") feedUrl(" + feedUrl
                + ") profileImageUrl(" + profileImageUrl
                + ") allArticleUrl(" + allArticleUrl + ")";
    }

    private Member(Parcel parcel) {
        allArticleUrl = parcel.readString();
        feedUrl = parcel.readString();
        profileImageUrl = parcel.readString();
        name = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(allArticleUrl);
        parcel.writeString(feedUrl);
        parcel.writeString(profileImageUrl);
        parcel.writeString(name);
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
        return Favorite.exist(context, feedUrl);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.ProfileWidget.KEY_NAME, name);
        cv.put(NogiFeedContent.ProfileWidget.KEY_IMAGE_URL, profileImageUrl);
        cv.put(NogiFeedContent.ProfileWidget.KEY_ARTICLE_URL, allArticleUrl);
        cv.put(NogiFeedContent.ProfileWidget.KEY_FEED_URL, feedUrl);
        return cv;
    }

}
