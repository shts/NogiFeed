package android.shts.jp.nogifeed.common;

public interface IPreferences {

    public class Key {
        /*

        common preference

        - is first boot this application.

         */
        public static final String FIRST_BOOT = "pref_first_boot";

        /*

        favorite member preferences.

        - use favorite function

        - notify when push member update blog.

         */
        public static final String FAVORITE = "pref_favorite";
        public static final String NOTIFICATION = "pref_notification";
    }
}
