
package shts.jp.android.nogifeed.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NogiFeedDatabaseHelper extends SQLiteOpenHelper {

    //@formatter:off
    private static final String CREATE_FAVORITE_TABLE_SQL = "CREATE TABLE "
            + NogiFeedContent.TABLE_FAVORITE + "("
            + NogiFeedContent.Favorite.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NogiFeedContent.Favorite.KEY_LINK + " TEXT NOT NULL"
            + ")";

    //@formatter:on
    private static final String DROP_FAVORITE_TABLE_SQL = "DROP TABLE IF EXISTS "
            + NogiFeedContent.TABLE_FAVORITE;

    //@formatter:off
    private static final String CREATE_PROFILE_WIDGET_TABLE_SQL = "CREATE TABLE "
            + NogiFeedContent.TABLE_PROFILE_WIDGET + "("
            + NogiFeedContent.ProfileWidget.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NogiFeedContent.ProfileWidget.KEY_WIDGET_ID + " INTEGER,"
            + NogiFeedContent.ProfileWidget.KEY_NAME + " TEXT NOT NULL,"
            + NogiFeedContent.ProfileWidget.KEY_ARTICLE_URL + " TEXT NOT NULL,"
            + NogiFeedContent.ProfileWidget.KEY_FEED_URL + " TEXT NOT NULL,"
            + NogiFeedContent.ProfileWidget.KEY_IMAGE_URL + " TEXT NOT NULL"
            + ")";

    //@formatter:on
    private static final String DROP_PROFILE_WIDGET_TABLE_SQL = "DROP TABLE IF EXISTS "
            + NogiFeedContent.TABLE_PROFILE_WIDGET;

    public NogiFeedDatabaseHelper(Context context) {
        super(context, NogiFeedContent.DATABASE_NAME, null, NogiFeedContent.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE_MEMBER_TABLE_SQL
        db.execSQL(CREATE_FAVORITE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL(DROP_FAVORITE_TABLE_SQL);
        // Create tables again
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(CREATE_PROFILE_WIDGET_TABLE_SQL);
        }
//        onCreate(db);
    }
}
