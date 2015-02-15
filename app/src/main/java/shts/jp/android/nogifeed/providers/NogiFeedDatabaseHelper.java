
package shts.jp.android.nogifeed.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NogiFeedDatabaseHelper extends SQLiteOpenHelper {

    //@formatter:off
    private static final String CREATE_FAVORITE_TABLE_SQL = "CREATE TABLE "
    + shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE + "("
    + shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
    + shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.KEY_LINK + " TEXT NOT NULL"
    + ")";
        
    //@formatter:on
    private static final String DROP_FAVORITE_TABLE_SQL = "DROP TABLE IF EXISTS "
            + shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE;
    
    public NogiFeedDatabaseHelper(Context context) {
        super(context, shts.jp.android.nogifeed.providers.NogiFeedContent.DATABASE_NAME, null, shts.jp.android.nogifeed.providers.NogiFeedContent.DATABASE_VERSION);
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
        onCreate(db);
    }
}
