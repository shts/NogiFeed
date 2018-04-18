package shts.jp.android.nogifeed.db

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import android.support.annotation.NonNull
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = FavoriteDao.TABLE_NAME)
data class Favorite2(
        @PrimaryKey(autoGenerate = true)
        @NonNull
        @ColumnInfo(name = "id")
        val id: Int,
        @ColumnInfo(name = "member_id")
        val memberId: Int
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = UnreadDao.TABLE_NAME)
data class Unread(
        @PrimaryKey(autoGenerate = true)
        @NonNull
        @ColumnInfo(name = "id")
        val id: Int,
        @ColumnInfo(name = "member_id")
        val memberId: Int,
        @ColumnInfo(name = "article_url")
        val articleUrl: String
) : Parcelable
