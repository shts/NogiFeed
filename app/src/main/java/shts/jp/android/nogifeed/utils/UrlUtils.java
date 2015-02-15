package shts.jp.android.nogifeed.utils;

public class UrlUtils {

    public static final String FEED_ALL_URL = "http://blog.nogizaka46.com/atom.xml";
    public static final String FEED_MEMBER_URL_SCHEME = "http://blog.nogizaka46.com/";
    public static final String FEED_MEMBER_URL_SUFFIX = "/atom.xml";

//    public static String getMemberFeedUrl(Member member) {
//        // http://blog.nogizaka46.com/rina.ikoma/atom.xml
//        return FEED_MEMBER_URL_SCHEME + member.firstName + member.lastName + FEED_MEMBER_URL_SUFFIX;
//    }

    /**
     * Get [member's feed url] from [member's a article url].
     * @param articleUrl member's a article url -> http://blog.nogizaka46.com/kana.nakada/2014/12/021877.php
     * @return member's feed url. -> http://blog.nogizaka46.com/kana.nakada/atom.xml
     */
    public static String getMemberFeedUrl(String articleUrl) {
        final String[] name = StringUtils.getFullNameFromArticleUrl(articleUrl);
        final String firstName = name[StringUtils.INDEX_FIRST_NAME];
        if (name.length == 1) { // kensyusei or unnei
            return FEED_MEMBER_URL_SCHEME + firstName + FEED_MEMBER_URL_SUFFIX;
        }
        final String lastName = name[StringUtils.INDEX_LAST_NAME];
        return FEED_MEMBER_URL_SCHEME + firstName + "." + lastName + FEED_MEMBER_URL_SUFFIX;
    }

//    public static final String MEMBER_PROFILE_URL_SCHEME = "http://www.nogizaka46.com/";
//    public static final String MEMBER_PROFILE_URL_PREFIX = "smph/member/detail/";
//    public static final String MEMBER_PROFILE_URL_SUFFIX = ".php";

//    public static String getMemberProfileUrl(Member member) {
//        // http://www.nogizaka46.com/smph/member/detail/ikomarina.php
//        return MEMBER_PROFILE_URL_SCHEME + MEMBER_PROFILE_URL_PREFIX +
//                member.lastName + member.firstName + MEMBER_PROFILE_URL_SUFFIX;
//    }

//    public static String getMemberProfileUrl(String firstName, String lastName) {
//        // http://www.nogizaka46.com/smph/member/detail/ikomarina.php
//        return MEMBER_PROFILE_URL_SCHEME + MEMBER_PROFILE_URL_PREFIX
//                + lastName + firstName + MEMBER_PROFILE_URL_SUFFIX;
//    }

    public static final String MEMBER_IMAGE_URL_SCHEME = "http://img.nogizaka46.com/";
    public static final String MEMBER_IMAGE_URL_PREFIX = "www/smph/member/img/";
    public static final String MEMBER_IMAGE_URL_SUFFIX = "_prof.jpg";

    /**
     * Get [member's feed url] from [member's image url].
     * [member's feed url] -> http://blog.nogizaka46.com/marika.ito/atom.xml
     * [member's image url] -> http://img.nogizaka46.com/www/smph/member/img/itoumarika_prof.jpg
     * @param memberFeedUrl member's all article url. http://blog.nogizaka46.com/marika.ito/atom.xml
     * @return member's image url.
     */
    public static String getMemberImageUrlFromFeedUrl(String memberFeedUrl) {
        String[] name = StringUtils.getFullNameFromArticleUrl(memberFeedUrl);
        if (name.length == 1) {
            // kensyusei
            return null;
        } else {
            return createMemberImageUrlFrom(
                    name[StringUtils.INDEX_FIRST_NAME], name[StringUtils.INDEX_LAST_NAME]);
        }
    }

    /**
     * Get [member's all article url] from [member image url].
     * [member's all article url] -> http://blog.nogizaka46.com/manatsu.akimoto/smph/
     * [member's image url] -> http://img.nogizaka46.com/www/smph/member/img/akimotomanatsu_prof.jpg
     * @param articleUrl member's all article url. http://blog.nogizaka46.com/manatsu.akimoto/smph/
     * @return member's image url.
     */
    public static String getImageUrlFromArticleUrl(String articleUrl) {
        String[] name = StringUtils.getFullNameFromAllArticleUrl(articleUrl);
        if (name.length == 1) {
            // kensyusei
            return null;
        } else {
            return createMemberImageUrlFrom(
                    name[StringUtils.INDEX_FIRST_NAME], name[StringUtils.INDEX_LAST_NAME]);
        }
    }

    /**
     * Create [member's profile image url] from [member's full name].
     * @param firstName Member's first name.
     * @param lastName Member's last name.
     * @return [member's profile image url]. -> http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg
     */
    private static String createMemberImageUrlFrom(String firstName, String lastName) {
        // http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg
        // eto saito ito nojo -> +u
        return MEMBER_IMAGE_URL_SCHEME + MEMBER_IMAGE_URL_PREFIX
                + StringUtils.addCharU(lastName) + firstName + MEMBER_IMAGE_URL_SUFFIX;
    }
}
