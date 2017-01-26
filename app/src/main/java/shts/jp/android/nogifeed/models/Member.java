package shts.jp.android.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

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
public class Member implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name_main")
    @Expose
    private String nameMain;
    @SerializedName("name_sub")
    @Expose
    private String nameSub;
    @SerializedName("blog_url")
    @Expose
    private String blogUrl;
    @SerializedName("rss_url")
    @Expose
    private String rssUrl;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("blood_type")
    @Expose
    private String bloodType;
    @SerializedName("constellation")
    @Expose
    private String constellation;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("favorite")
    @Expose
    private Integer favorite;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("status")
    @Expose
    private String status;

    public Member(Integer id, String nameMain, String nameSub, String blogUrl, String rssUrl, String imageUrl, String birthday, String bloodType, String constellation, String height, String createdAt, String updatedAt, Integer favorite, String key, String status) {
        this.id = id;
        this.nameMain = nameMain;
        this.nameSub = nameSub;
        this.blogUrl = blogUrl;
        this.rssUrl = rssUrl;
        this.imageUrl = imageUrl;
        this.birthday = birthday;
        this.bloodType = bloodType;
        this.constellation = constellation;
        this.height = height;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.favorite = favorite;
        this.key = key;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getNameMain() {
        return nameMain;
    }

    public String getNameSub() {
        return nameSub;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getConstellation() {
        return constellation;
    }

    public String getHeight() {
        return height;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public String getKey() {
        return key;
    }

    public ArrayList<String> getStatus() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(status);
            final int N = array.length();
            for (int i = 0; i < N; i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    protected Member(Parcel in) {
        nameMain = in.readString();
        nameSub = in.readString();
        blogUrl = in.readString();
        rssUrl = in.readString();
        imageUrl = in.readString();
        birthday = in.readString();
        bloodType = in.readString();
        constellation = in.readString();
        height = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        key = in.readString();
        status = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameMain);
        dest.writeString(nameSub);
        dest.writeString(blogUrl);
        dest.writeString(rssUrl);
        dest.writeString(imageUrl);
        dest.writeString(birthday);
        dest.writeString(bloodType);
        dest.writeString(constellation);
        dest.writeString(height);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(key);
        dest.writeString(status);
    }
}
