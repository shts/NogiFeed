package shts.jp.android.nogifeed.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;

public class Blog implements Parcelable {

    private static final String TAG = Blog.class.getSimpleName();

    private String entryObjectId;
    private String url;
    private String title;
    private String author;
    private String authorId;
    private String authorImageUrl;
    private ArrayList<String> uploadedThumbnailUrlList;
    private ArrayList<String> uploadedRawUrlList;

    public Blog(Entry entry) {
        entryObjectId = entry.getObjectId();
        url = entry.getBlogUrl();
        title = entry.getTitle();
        author = entry.getAuthor();
        authorId = entry.getAuthorId();
        authorImageUrl = entry.getAuthorImageUrl();
        uploadedThumbnailUrlList = (ArrayList<String>) entry.getUploadedThumbnailUrlList();
        uploadedRawUrlList = (ArrayList<String>) entry.getUploadedRawImageUrlList();
    }

    public Blog(JSONObject json) {
        try {
            entryObjectId = json.getString("_entryObjectId");
            url = json.getString("_url");
            title = json.getString("_title");
            author = json.getString("_author");
            authorId = json.getString("_author_id");
            authorImageUrl = json.getString("_author_image_url");

            final JSONArray thumbnailArray = json.getJSONArray("_uploaded_thumbnail_url");
            uploadedThumbnailUrlList = new ArrayList<>();
            final int N = thumbnailArray.length();
            for (int i = 0; i < N; i++) {
                uploadedThumbnailUrlList.add(thumbnailArray.getString(i));
            }

            final JSONArray rawArray = json.getJSONArray("_uploaded_raw_image_url");
            uploadedRawUrlList = new ArrayList<>();
            final int M = rawArray.length();
            for (int i = 0; i < M; i++) {
                uploadedRawUrlList.add(rawArray.getString(i));
            }
        } catch (JSONException e) {
            Logger.w(TAG, "illegal data received");
        }
    }

    public String getEntryObjectId() { return entryObjectId; }
    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getAuthorId() { return authorId; }
    public String getAuthorImageUrl() { return authorImageUrl; }
    public ArrayList<String> getUploadedThumbnailUrlList() { return uploadedThumbnailUrlList; }
    public ArrayList<String> getUploadedRawUrlList() { return uploadedRawUrlList; }

    protected Blog(Parcel in) {
        entryObjectId = in.readString();
        url = in.readString();
        title = in.readString();
        author = in.readString();
        authorId = in.readString();
        authorImageUrl = in.readString();
        if (in.readByte() == 0x01) {
            uploadedThumbnailUrlList = new ArrayList<>();
            in.readList(uploadedThumbnailUrlList, String.class.getClassLoader());
        } else {
            uploadedThumbnailUrlList = null;
        }
        if (in.readByte() == 0x01) {
            uploadedRawUrlList = new ArrayList<>();
            in.readList(uploadedRawUrlList, String.class.getClassLoader());
        } else {
            uploadedRawUrlList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(entryObjectId);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(authorId);
        dest.writeString(authorImageUrl);
        if (uploadedThumbnailUrlList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(uploadedThumbnailUrlList);
        }
        if (uploadedRawUrlList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(uploadedRawUrlList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Blog> CREATOR = new Parcelable.Creator<Blog>() {
        @Override
        public Blog createFromParcel(Parcel in) {
            return new Blog(in);
        }

        @Override
        public Blog[] newArray(int size) {
            return new Blog[size];
        }
    };
}