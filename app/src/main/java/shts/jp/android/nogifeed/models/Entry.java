package shts.jp.android.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.utils.DateUtils;

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
public class Entry implements Parcelable {

    @SerializedName(value = "__id", alternate = {"id", "_id"})
    @Expose
    private Integer id;
    @SerializedName(value = "__title", alternate = {"title", "_title"})
    @Expose
    private String title;
    @SerializedName(value = "__url", alternate = {"url", "_url"})
    @Expose
    private String url;
    @SerializedName(value = "__published2", alternate = {"published2", "_published2"})
    @Expose
    private String published2;
    @SerializedName(value = "__original_raw_image_urls", alternate = {"original_raw_image_urls", "_original_raw_image_urls"})
    @Expose
    private String originalRawImageUrls;
    @SerializedName(value = "__original_thumbnail_urls", alternate = {"original_thumbnail_urls", "_original_thumbnail_urls"})
    @Expose
    private String originalThumbnailUrls;
    @SerializedName(value = "__uploaded_raw_image_urls", alternate = {"uploaded_raw_image_urls", "_uploaded_raw_image_urls"})
    @Expose
    private String uploadedRawImageUrls;
    @SerializedName(value = "__uploaded_thumbnail_urls", alternate = {"uploaded_thumbnail_urls", "_uploaded_thumbnail_urls"})
    @Expose
    private String uploadedThumbnailUrls;
    @SerializedName(value = "__member_id", alternate = {"member_id", "_member_id"})
    @Expose
    private Integer memberId;
    @SerializedName(value = "__member_name", alternate = {"member_name", "_member_name"})
    @Expose
    private String memberName;
    @SerializedName(value = "__member_image_url", alternate = {"member_image_url", "_member_image_url"})
    @Expose
    private String memberImageUrl;

    public Entry(Integer id, String title, String url, String published, String originalRawImageUrls, String originalThumbnailUrls, String uploadedRawImageUrls, String uploadedThumbnailUrls, Integer memberId, String memberName, String memberImageUrl) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.published2 = published;
        this.originalRawImageUrls = originalRawImageUrls;
        this.originalThumbnailUrls = originalThumbnailUrls;
        this.uploadedRawImageUrls = uploadedRawImageUrls;
        this.uploadedThumbnailUrls = uploadedThumbnailUrls;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberImageUrl = memberImageUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPublished() {
        return DateUtils.parse(this.published2);
    }

    public List<String> getOriginalRawImageUrls() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(originalRawImageUrls);
            final int N = array.length();
            for (int i = 0; i < N; i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getOriginalThumbnailUrls() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(originalThumbnailUrls);
            final int N = array.length();
            for (int i = 0; i < N; i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getUploadedRawImageUrls() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(uploadedRawImageUrls);
            final int N = array.length();
            for (int i = 0; i < N; i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getUploadedThumbnailUrls() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(uploadedThumbnailUrls);
            final int N = array.length();
            for (int i = 0; i < N; i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberImageUrl() {
        return memberImageUrl;
    }

    protected Entry(Parcel in) {
        title = in.readString();
        url = in.readString();
        published2 = in.readString();
        originalRawImageUrls = in.readString();
        originalThumbnailUrls = in.readString();
        uploadedRawImageUrls = in.readString();
        uploadedThumbnailUrls = in.readString();
        memberName = in.readString();
        memberImageUrl = in.readString();
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(published2);
        dest.writeString(originalRawImageUrls);
        dest.writeString(originalThumbnailUrls);
        dest.writeString(uploadedRawImageUrls);
        dest.writeString(uploadedThumbnailUrls);
        dest.writeString(memberName);
        dest.writeString(memberImageUrl);
    }
}