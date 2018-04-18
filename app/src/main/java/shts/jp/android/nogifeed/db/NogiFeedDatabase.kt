package shts.jp.android.nogifeed.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(Favorite2::class), (Unread::class)], version = 1)
abstract class NogiFeedDatabase : RoomDatabase() {

    abstract fun favoriteDao() : FavoriteDao

    abstract fun unreadDao() : UnreadDao

    companion object {
        private const val DATABASE_NAME = "nogifeed2.db"

        @Volatile private var INSTANCE: NogiFeedDatabase? = null

        fun getInstance(context: Context): NogiFeedDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        NogiFeedDatabase::class.java, DATABASE_NAME)
                        .build()
    }
}
