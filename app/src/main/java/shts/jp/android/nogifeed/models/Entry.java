package shts.jp.android.nogifeed.models;

import android.content.Context;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

@ParseClassName("Entry")
public class Entry extends ParseObject {

    public static final String KEY = Entry.class.getSimpleName();

    public Entry() {
        // for ParseObject
    }

    public static Entry getReference(String objectId) {
        return ParseObject.createWithoutData(Entry.class, objectId);
    }

    public static ParseQuery<Entry> getQuery(String objectId) {
        ParseQuery<Entry> query = ParseQuery.getQuery(Entry.class);
        query.whereEqualTo("objectId", objectId);
        query.orderByDescending("published");
        return query;
    }

    public static ParseQuery<Entry> getQuery() {
        ParseQuery<Entry> query = ParseQuery.getQuery(Entry.class);
        query.orderByDescending("published");
        return query;
    }

    public static ParseQuery<Entry> getQuery(int limit, int skip) {
        ParseQuery<Entry> query = ParseQuery.getQuery(Entry.class);
        query.orderByDescending("published");
        query.setLimit(limit);
        query.setSkip(skip);
        return query;
    }

    public void archive(Context context) {
        // TODO: download image file
        pinInBackground();
    }

    public void encache() {
        this.cache = new Cache();
    }

    private Cache cache;
    public class Cache {
        String author, authorId, title, body, blogUrl,
         year, month, day, dayweek;
        Date entryDate, published;
        List<String> thumbnailUrls, rawImageUrls;
        Cache() {
            author = getAuthor();
            title = getTitle();
            blogUrl = getBlogUrl();
            entryDate = getEntryDate();
            published = getPublishedDate();
        }
    }

    // --------------------------------------------------
    // Accesser
    // --------------------------------------------------

    public String getAuthor() {
        if (cache != null) return cache.author;
        return getString("author");
    }

    public String getAuthorId() {
        return getString("author_id");
    }

    public String getAuthorImageUrl() {
        return getString("author_image_url");
    }

    public String getTitle() {
        if (cache != null) return cache.title;
        return getString("title");
    }

    public String getBody() {
        return getString("body");
    }

    public String getBlogUrl() {
        if (cache != null) return cache.blogUrl;
        return getString("url");
    }

    public Date getEntryDate() {
        if (cache != null) return cache.entryDate;
        return getDate("date");
    }

    public Date getPublishedDate() {
        if (cache != null) return cache.published;
        return getDate("published");
    }

    public List<String> getOriginalRawImageUrlList() {
        return getList("original_raw_img_url");
    }

    public List<String> getThumbnailImageUrlList() {
        return getList("original_thumbnail_url");
    }

    public List<String> getUploadedThumbnailUrlList() {
        return getList("uploaded_thumbnail_url");
    }

    public List<String> getUploadedRawImageUrlList() {
        return getList("uploaded_raw_image_url");
    }

    public String getYear() {
        return getString("year");
    }

    public String getMonth() {
        return getString("month");
    }

    public String getDay() {
        return getString("day");
    }

    public String getDayweek() {
        return getString("dayweek");
    }

}