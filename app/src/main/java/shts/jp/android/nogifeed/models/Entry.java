package shts.jp.android.nogifeed.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import shts.jp.android.nogifeed.models.eventbus.BusHolder;

@ParseClassName("Entry")
public class Entry extends ParseObject {

    public static final String KEY = Entry.class.getSimpleName();

    public Entry() {
        // for ParseObject
    }

    public static Entry getReference(String objectId) {
        return ParseObject.createWithoutData(Entry.class, objectId);
    }

    public static void findById(int limit, int skip, String memberObjectId) {
        ParseQuery<Entry> q = query(limit, skip);
        q.whereEqualTo("author_id", memberObjectId);
        q.findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                BusHolder.get().post(new GotAllEntryCallback.FindById(entries, e));
            }
        });
    }

    public static void findById(int limit, int skip, List<Favorite> favorites) {
        List<String> ids = new ArrayList<>();
        for (Favorite favorite : favorites) {
            ids.add(favorite.getMemberObjectId());
        }
        ParseQuery<Entry> q = query(limit, skip);
        q.whereContainedIn("author_id", ids);
        q.orderByDescending("published");
        q.findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                BusHolder.get().post(new GotAllEntryCallback.FindById(entries, e));
            }
        });
    }

    public static void all(int limit, int skip) {
        query(limit, skip).findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                BusHolder.get().post(new GotAllEntryCallback.All(entries, e));
            }
        });
    }

    public static void next(int limit, int skip) {
        query(limit, skip).findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                BusHolder.get().post(new GotAllEntryCallback.Next(entries, e));
            }
        });
    }

    private static ParseQuery<Entry> query(int limit, int skip) {
        ParseQuery<Entry> query = ParseQuery.getQuery(Entry.class);
        query.orderByDescending("published");
        query.setLimit(limit);
        query.setSkip(skip);
        return query;
    }

    public static class GotAllEntryCallback {
        public final List<Entry> entries;
        public final ParseException e;
        GotAllEntryCallback(List<Entry> entries, ParseException e) {
            this.entries = entries;
            this.e = e;
        }
        public static class All extends GotAllEntryCallback {
            All(List<Entry> entries, ParseException e) { super(entries, e); }
        }
        public static class Next extends GotAllEntryCallback {
            Next(List<Entry> entries, ParseException e) { super(entries, e); }
        }
        public static class FindById extends GotAllEntryCallback {
            FindById(List<Entry> entries, ParseException e) { super(entries, e); }
        }
        public boolean hasError() {
            return e != null || entries == null || entries.isEmpty();
        }
    }

    public String getAuthor() {
        return getString("author");
    }

    public String getAuthorId() {
        return getString("author_id");
    }

    public String getAuthorImageUrl() {
        return getString("author_image_url");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getBlogUrl() {
        return getString("url");
    }

    public Date getEntryDate() {
        return getDate("date");
    }

    public Date getPublishedDate() {
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