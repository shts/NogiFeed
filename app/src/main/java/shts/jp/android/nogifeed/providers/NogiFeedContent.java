
package shts.jp.android.nogifeed.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class NogiFeedContent {
    public static final String AUTHORITY = "android.shts.jp.nogifeed.providers.nogifeed";
    public static final String DATABASE_NAME = "nogifeed.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_FAVORITE = "favorite";

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
}
