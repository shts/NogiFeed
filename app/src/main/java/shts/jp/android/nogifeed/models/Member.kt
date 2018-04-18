package shts.jp.android.nogifeed.models

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class Members2 : ArrayList<Member2>()

/**
t.string :name_main
t.string :name_sub
t.string :blog_url
t.string :rss_url
t.string :status
t.string :image_url
t.string :birthday
t.string :blood_type
t.string :constellation
t.string :height
t.integer :favorite
t.string :key
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Member2(
        @SerializedName("id")
        @Expose
        private val id: Int,
        @SerializedName("name_main")
        @Expose
        private var nameMain: String?,
        @SerializedName("name_sub")
        @Expose
        private var nameSub: String?,
        @SerializedName("blog_url")
        @Expose
        private var blogUrl: String?,
        @SerializedName("rss_url")
        @Expose
        private var rssUrl: String?,
        @SerializedName("image_url")
        @Expose
        private var imageUrl: String?,
        @SerializedName("birthday")
        @Expose
        private var birthday: String?,
        @SerializedName("blood_type")
        @Expose
        private var bloodType: String?,
        @SerializedName("constellation")
        @Expose
        private var constellation: String?,
        @SerializedName("height")
        @Expose
        private var height: String?,
        @SerializedName("created_at")
        @Expose
        private var createdAt: String?,
        @SerializedName("updated_at")
        @Expose
        private var updatedAt: String?,
        @SerializedName("favorite")
        @Expose
        private val favorite: Int,
        @SerializedName("key")
        @Expose
        private var key: String?,
        @SerializedName("status")
        @Expose
        private var status: String?
) : Parcelable
