package shts.jp.android.nogifeed.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface FavoriteDao {

    companion object {
        const val TABLE_NAME = "favorites"
    }

    @Insert(onConflict = REPLACE)
    fun insert(data: Favorite2)

    @Query("DELETE FROM $TABLE_NAME WHERE member_id = :memberId")
    fun delete(memberId: Int)

    @Query("SELECT * FROM $TABLE_NAME")
    fun favorites(): LiveData<List<Favorite2>>

    @Query("SELECT * FROM $TABLE_NAME")
    fun getFavorites(): List<Favorite2>

}

@Dao
interface UnreadDao {

    companion object {
        const val TABLE_NAME = "unreads"
    }

    @Insert
    fun insert(data: Unread)

    @Query("SELECT * FROM $TABLE_NAME")
    fun unreads(): LiveData<List<Unread>>

}


