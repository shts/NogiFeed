package shts.jp.android.nogifeed.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import shts.jp.android.nogifeed.common.Logger;

public class NogizakaUrl implements Parcelable {

    private static final String TAG = NogizakaUrl.class.getSimpleName();
    protected String urlString;

    protected NogizakaUrl(Parcel in) {
        urlString = in.readString();
    }

    private NogizakaUrl() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(urlString);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NogizakaUrl> CREATOR = new Parcelable.Creator<NogizakaUrl>() {
        @Override
        public NogizakaUrl createFromParcel(Parcel in) {
            return new NogizakaUrl(in);
        }

        @Override
        public NogizakaUrl[] newArray(int size) {
            return new NogizakaUrl[size];
        }
    };

    public String text() {
        return this.urlString;
    }

    /**
     * ex) http://blog.nogizaka46.com/marika.ito/atom.xml
     */
    public static class Feed extends NogizakaUrl implements Parcelable {
        public static final String FEED_ALL_URL = "http://blog.nogizaka46.com/atom.xml";
        private static final String FEED_MEMBER_URL_SCHEME = "http://blog.nogizaka46.com/";
        private static final String FEED_MEMBER_URL_SUFFIX = "/atom.xml";

        protected Feed(Parcel in) {
            urlString = in.readString();
        }

        private Feed() {}

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(urlString);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<NogizakaUrl> CREATOR = new Parcelable.Creator<NogizakaUrl>() {
            @Override
            public NogizakaUrl createFromParcel(Parcel in) {
                return new NogizakaUrl(in);
            }

            @Override
            public NogizakaUrl[] newArray(int size) {
                return new NogizakaUrl[size];
            }
        };

        public Feed(String url) {
            this.urlString = url;
        }

        public ProfileImage toProfileImage() {
            return ProfileImage.from(this);
        }

        public static Feed from(Article article) {
            Name name = new Name(article);
            return new Feed(createFeedUrl(name.first, name.last));
        }

        public static Feed from(AllArticles allArticles) {
            Name name = new Name(allArticles);
            return new Feed(createFeedUrl(name.first, name.last));
        }

        private static String createFeedUrl(String firstName, String lastName) {
            if (TextUtils.isEmpty(lastName)) {
                return FEED_MEMBER_URL_SCHEME + firstName + FEED_MEMBER_URL_SUFFIX;
            } else {
                return FEED_MEMBER_URL_SCHEME + firstName + "." + lastName + FEED_MEMBER_URL_SUFFIX;
            }
        }
    }

    /**
     * ex) http://blog.nogizaka46.com/kana.nakada/2014/12/021877.php
     */
    public static class Article extends NogizakaUrl {
        public Article(String url) {
            urlString = url;
        }
        public ProfileImage toProfileImage() {
            return ProfileImage.from(this);
        }
    }

    /**
     * ex) http://blog.nogizaka46.com/manatsu.akimoto/smph/
     */
    public static class AllArticles extends NogizakaUrl {
        public AllArticles(String url) {
            urlString = url;
        }
        public ProfileImage toProfileImage() {
            return ProfileImage.from(this);
        }
    }

    /**
     * ex) http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg
     */
    public static class ProfileImage extends NogizakaUrl {

        private static final String MEMBER_IMAGE_URL_SCHEME = "http://img.nogizaka46.com/";
        private static final String MEMBER_IMAGE_URL_PREFIX = "www/smph/member/img/";
        private static final String MEMBER_IMAGE_URL_SUFFIX = "_prof.jpg";

        ProfileImage(String url) {
            urlString = url;
        }

        public static ProfileImage from(Feed feed) {
            Name name = new Name(feed);
            return new ProfileImage(createImageUrlFrom(name.first, name.last));
        }
        public static ProfileImage from(Article article) {
            Name name = new Name(article);
            return new ProfileImage(createImageUrlFrom(name.first, name.last));
        }
        public static ProfileImage from(AllArticles allArticles) {
            Name name = new Name(allArticles);
            return new ProfileImage(createImageUrlFrom(name.first, name.last));
        }
        /**
         * Create [member's profile image url] from [member's full name].
         * @param firstName Member's first name.
         * @param lastName Member's last name.
         * @return [member's profile image url]. -> http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg
         */
        private static String createImageUrlFrom(String firstName, String lastName) {
            if (TextUtils.isEmpty(lastName)) {
                return "";
            } else {
                return MEMBER_IMAGE_URL_SCHEME + MEMBER_IMAGE_URL_PREFIX
                        + lastName + firstName + MEMBER_IMAGE_URL_SUFFIX;
            }
        }
    }

    public static class Name {

        private static final int INDEX_FIRST_NAME = 0;
        private static final int INDEX_LAST_NAME = 1;

        private static final String[] NEED_ADD_CHAR_LAST_NAME = {
                "ito", "saito", "noujo", "eto"
        };

        String first;
        String last;

        Name(Feed feed) {
            parse(feed.urlString);
        }
        Name(Article article) {
            parse(article.urlString);
        }
        Name(AllArticles allArticles) {
            parse(allArticles.urlString);
        }

        public static String[] getFullNameFromArticleUrl(String article) {
            final String[] fullName = article.split("/");
            final String[] fullNameArray = fullName[3].split("\\.");
            for (int i = 0; i < fullNameArray.length; i++) {
                Logger.d(TAG, "index : " + i + " name : " + fullNameArray[i]);
            }
            return fullNameArray;
        }

        private void parse(String url) {
            final String[] fullName = url.split("/");
            final String[] fullNameArray = fullName[3].split("\\.");
            for (int i = 0; i < fullNameArray.length; i++) {
                Logger.d(TAG, "index : " + i + " name : " + fullNameArray[i]);
            }
            Logger.v(TAG, "parse : url(" + url + ")");
            if (fullNameArray.length == 1) {
                first = fullNameArray[INDEX_FIRST_NAME];
            } else {
                first = fullNameArray[INDEX_FIRST_NAME];
                last = addCharU(fullNameArray[INDEX_LAST_NAME]);
            }
        }

        private String addCharU(String lastName) {
            for (String needAddCharName : NEED_ADD_CHAR_LAST_NAME) {
                if (needAddCharName.equals(lastName)) {
                    lastName += 'u';
                }
            }
            return lastName;
        }
    }
}
