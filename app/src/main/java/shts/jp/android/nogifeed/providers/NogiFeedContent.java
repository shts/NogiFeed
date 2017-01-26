
package shts.jp.android.nogifeed.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class NogiFeedContent {

    public static final String AUTHORITY = "android.shts.jp.nogifeed.providers.nogifeed";
    public static final String DATABASE_NAME = "nogifeed.db";
    /**
     * Version 1. add TABLE_FAVORITE
     * ------------------------------------------
     * Version 2. add TABLE_PROFILE_WIDGET
     * add TABLE_UNREAD
     * ------------------------------------------
     * Version 3. delete all table
     * ------------------------------------------
     * Version 4. add TABLE_FAVORITE
     * add TABLE_PROFILE_WIDGET
     * add TABLE_UNREAD
     */
    public static final int DATABASE_VERSION = 4;

    public static final String TABLE_FAVORITE = "favorite";
    public static final String TABLE_PROFILE_WIDGET = "profile_widget";
    public static final String TABLE_UNREAD = "unread";

    public static final class Favorite implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorite");
        public static final Uri CONTENT_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/favorite/filter");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.keyakifeed.favorite";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.keyakifeed.favorite";

        public @interface Key {
            String ID = BaseColumns._ID;
            String MEMBER_ID = "member_id";
        }

        public static final String[] sProjection = {
                Key.ID, Key.MEMBER_ID
        };

    }

    public static final class ProfileWidget implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/profile_widget");
        public static final Uri CONTENT_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/profile_widget/filter");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nogifeed.profile_widget";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nogifeed.profile_widget";

        // Place Table Columns names
        public @interface Key {
            String ID = BaseColumns._ID;
            String WIDGET_ID = "widget_id";
            String MEMBER_ID = "member_id";
        }

        public static final String[] sProjection = {
                Key.ID, Key.WIDGET_ID, Key.MEMBER_ID
        };
    }

    public static final class UnRead implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/unread");
        public static final Uri CONTENT_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/unread/filter");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.keyakifeed.unread";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.keyakifeed.unread";

        public @interface Key {
            String ID = BaseColumns._ID;
            /**
             * Widget用
             */
            String MEMBER_ID = "member_id";
            String ARTICLE_URL = "article_url";
        }

        public @interface Value {
            int OFFICIAL_REPORT = -10;
        }

        public static final String[] sProjection = {
                Key.ID, Key.MEMBER_ID, Key.ARTICLE_URL
        };

    }
}
