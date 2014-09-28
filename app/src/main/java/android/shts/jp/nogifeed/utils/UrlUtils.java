package android.shts.jp.nogifeed.utils;

import android.shts.jp.nogifeed.models.Member;

public class UrlUtils {

    public static final String FEED_ALL_URL = "http://blog.nogizaka46.com/atom.xml";

    public static final String FEED_MEMBER_URL_SCHEME = "http://blog.nogizaka46.com/";
    public static final String FEED_MEMBER_URL_SUFFIX = "/atom.xml";

    public static String getMemberFeedUrl(Member member) {
        // http://blog.nogizaka46.com/rina.ikoma/atom.xml
        return FEED_MEMBER_URL_SCHEME + member.firstName + member.lastName + FEED_MEMBER_URL_SUFFIX;
    }

    public static final String MEMBER_PROFILE_URL_SCHEME = "http://www.nogizaka46.com/";
    public static final String MEMBER_PROFILE_URL_PREFIX = "smph/member/detail/";
    public static final String MEMBER_PROFILE_URL_SUFFIX = ".php";

    public static String getMemberProfileUrl(Member member) {
        // http://www.nogizaka46.com/smph/member/detail/ikomarina.php
        return MEMBER_PROFILE_URL_SCHEME + MEMBER_PROFILE_URL_PREFIX +
                member.lastName + member.firstName + MEMBER_PROFILE_URL_SUFFIX;
    }

    public static String getMemberProfileUrl(String firstName, String lastName) {
        // http://www.nogizaka46.com/smph/member/detail/ikomarina.php
        return MEMBER_PROFILE_URL_SCHEME + MEMBER_PROFILE_URL_PREFIX
                + lastName + firstName + MEMBER_PROFILE_URL_SUFFIX;
    }

    public static final String MEMBER_IMAGE_URL_SCHEME = "http://img.nogizaka46.com/";
    public static final String MEMBER_IMAGE_URL_PREFIX = "www/smph/member/img/";
    public static final String MEMBER_IMAGE_URL_SUFFIX = "_prof.jpg";

    public static synchronized String getMemberImageUrl(String memberFeedUrl) {
        String[] name = StringUtils.createFullNameFrom(memberFeedUrl);
        if (name.length == 1) {
            // kensyusei
            return null;
        } else {
            return getMemberImageUrl(name[StringUtils.INDEX_FIRST_NAME], name[StringUtils.INDEX_LAST_NAME]);
        }
    }

    public static String getMemberImageUrl(String firstName, String lastName) {
        // http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg
        // eto saito ito nojo -> +u
        return MEMBER_IMAGE_URL_SCHEME + MEMBER_IMAGE_URL_PREFIX
                + StringUtils.addCharU(lastName) + firstName + MEMBER_IMAGE_URL_SUFFIX;
    }

}
