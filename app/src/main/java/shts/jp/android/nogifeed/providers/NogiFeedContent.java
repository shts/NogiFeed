
package shts.jp.android.nogifeed.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class NogiFeedContent {
    public static final String AUTHORITY = "android.shts.jp.nogifeed.providers.nogifeed";
    public static final String DATABASE_NAME = "nogifeed.db";
    /**
     * Version 1. add TABLE_FAVORITE
     * Version 2. add TABLE_PROFILE_WIDGET
     */
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_FAVORITE = "favorite";
    public static final String TABLE_PROFILE_WIDGET = "profile_widget";

    public static final class Favorite implements BaseColumns {
    	
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorite");
        public static final Uri CONTENT_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/favorite/filter");
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nogifeed.favorite";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nogifeed.favorite";
        
        // Place Table Columns names
        public static final String KEY_ID = "_id";
        public static final String KEY_LINK = "link";

    	public static final String[] sProjection = {
    		Favorite.KEY_ID,
    		Favorite.KEY_LINK,
    	};
    }

    public static final class ProfileWidget implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/profile_widget");
        public static final Uri CONTENT_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/profile_widget/filter");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nogifeed.profile_widget";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nogifeed.profile_widget";

        // Place Table Columns names
        public static final String KEY_ID = "_id";
        public static final String KEY_WIDGET_ID = "_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_IMAGE_URL = "image_url";
        public static final String KEY_ARTICLE_URL = "article_image";
        public static final String KEY_FEED_URL = "feed_url";

        public static final String[] sProjection = {
                ProfileWidget.KEY_ID,
                ProfileWidget.KEY_NAME,
                ProfileWidget.KEY_IMAGE_URL,
                ProfileWidget.KEY_ARTICLE_URL,
                ProfileWidget.KEY_FEED_URL,
        };
    }
}
