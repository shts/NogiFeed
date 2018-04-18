package shts.jp.android.nogifeed.models

import android.annotation.SuppressLint
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class Feeds : ArrayList<Feed>()

/**
 * t.string :title
 * t.string :url
 * t.string :member_id
 * t.string :original_raw_image_urls
 * t.string :original_thumbnail_urls
 * t.string :uploaded_raw_image_urls
 * t.string :uploaded_thumbnail_urls
 * t.string :published <- まちがえた
 * t.datetime :published2 <- こっちをつかう
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Feed(
        @SerializedName(value = "__id", alternate = ["id", "_id"])
        @Expose
        private val id: Int?,
        @SerializedName(value = "__title", alternate = ["title", "_title"])
        @Expose
        private var title: String?,
        @SerializedName(value = "__url", alternate = ["url", "_url"])
        @Expose
        private var url: String?,
        @SerializedName(value = "__published2", alternate = ["published2", "_published2"])
        @Expose
        private var published2: String?,
        @SerializedName(value = "__original_raw_image_urls", alternate = ["original_raw_image_urls", "_original_raw_image_urls"])
        @Expose
        private var originalRawImageUrls: String?,
        @SerializedName(value = "__original_thumbnail_urls", alternate = ["original_thumbnail_urls", "_original_thumbnail_urls"])
        @Expose
        private var originalThumbnailUrls: String?,
        @SerializedName(value = "__uploaded_raw_image_urls", alternate = ["uploaded_raw_image_urls", "_uploaded_raw_image_urls"])
        @Expose
        private var uploadedRawImageUrls: String?,
        @SerializedName(value = "__uploaded_thumbnail_urls", alternate = ["uploaded_thumbnail_urls", "_uploaded_thumbnail_urls"])
        @Expose
        private var uploadedThumbnailUrls: String?,
        @SerializedName(value = "__member_id", alternate = ["member_id", "_member_id"])
        @Expose
        private val memberId: Int?,
        @SerializedName(value = "__member_name", alternate = ["member_name", "_member_name"])
        @Expose
        private var memberName: String?,
        @SerializedName(value = "__member_image_url", alternate = ["member_image_url", "_member_image_url"])
        @Expose
        private var memberImageUrl: String?
) : Parcelable {

    fun getOriginalRawImageUrls(): List<String> =
            jsonArrayToArrayList(originalRawImageUrls)

    fun getOriginalThumbnailUrls(): List<String> =
            jsonArrayToArrayList(originalThumbnailUrls)

    fun getUploadedRawImageUrls(): List<String> =
            jsonArrayToArrayList(uploadedRawImageUrls)

    fun getUploadedThumbnailUrls(): List<String> =
            jsonArrayToArrayList(uploadedThumbnailUrls)

    private fun jsonArrayToArrayList(arrayString: String?): ArrayList<String> {
        val list = ArrayList<String>()
        if (TextUtils.isEmpty(arrayString)) return list

        try {
            val array = JSONArray(arrayString)
            (0 until array.length())
                    .mapTo(list) { array.getString(it) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return list
    }
}
