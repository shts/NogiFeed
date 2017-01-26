
package shts.jp.android.nogifeed.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class NogiFeedDatabaseHelper extends SQLiteOpenHelper {

    //@formatter:off
    private static final String CREATE_FAVORITE_TABLE_SQL = "CREATE TABLE "
            + NogiFeedContent.TABLE_FAVORITE + "("
            + NogiFeedContent.Favorite.Key.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NogiFeedContent.Favorite.Key.MEMBER_ID + " INTEGER NOT NULL"
            + ")";

    //@formatter:on
    private static final String DROP_FAVORITE_TABLE_SQL = "DROP TABLE IF EXISTS "
            + NogiFeedContent.TABLE_FAVORITE;

    //@formatter:off
    private static final String CREATE_PROFILE_WIDGET_TABLE_SQL = "CREATE TABLE "
            + NogiFeedContent.TABLE_PROFILE_WIDGET + "("
            + NogiFeedContent.ProfileWidget.Key.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NogiFeedContent.ProfileWidget.Key.WIDGET_ID + " INTEGER"
            + ")";

    //@formatter:on
    private static final String DROP_PROFILE_WIDGET_TABLE_SQL = "DROP TABLE IF EXISTS "
            + NogiFeedContent.TABLE_PROFILE_WIDGET;

    //@formatter:off
    private static final String CREATE_UNREAD_TABLE_SQL = "CREATE TABLE "
            + NogiFeedContent.TABLE_UNREAD + "("
            + NogiFeedContent.UnRead.Key.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NogiFeedContent.UnRead.Key.ARTICLE_URL + " TEXT NOT NULL,"
            + NogiFeedContent.UnRead.Key.MEMBER_ID + " TEXT NOT NULL"
            + ")";

    //@formatter:on
    private static final String DROP_UNREAD_TABLE_SQL = "DROP TABLE IF EXISTS "
            + NogiFeedContent.TABLE_UNREAD;


    NogiFeedDatabaseHelper(Context context) {
        super(context, NogiFeedContent.DATABASE_NAME, null, NogiFeedContent.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE_MEMBER_TABLE_SQL
        db.execSQL(CREATE_FAVORITE_TABLE_SQL);
        db.execSQL(CREATE_PROFILE_WIDGET_TABLE_SQL);
        db.execSQL(CREATE_UNREAD_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create tables again
        if (newVersion == 2) {
            db.execSQL(CREATE_PROFILE_WIDGET_TABLE_SQL);
            db.execSQL(CREATE_UNREAD_TABLE_SQL);
        }

        /**
         * Version 1. add TABLE_FAVORITE
         * ------------------------------------------
         * Version 2. add TABLE_PROFILE_WIDGET
         *            add TABLE_UNREAD
         * ------------------------------------------
         * Version 3. delete all table
         * ------------------------------------------
         * Version 4. add TABLE_FAVORITE
         *            add TABLE_PROFILE_WIDGET
         *            add TABLE_UNREAD
         */
        if (newVersion == 4) {
            // drop
            db.execSQL(DROP_FAVORITE_TABLE_SQL);
            db.execSQL(DROP_PROFILE_WIDGET_TABLE_SQL);
            db.execSQL(DROP_UNREAD_TABLE_SQL);
            // create
            db.execSQL(CREATE_FAVORITE_TABLE_SQL);
            db.execSQL(CREATE_PROFILE_WIDGET_TABLE_SQL);
            db.execSQL(CREATE_UNREAD_TABLE_SQL);
        }
    }
}
