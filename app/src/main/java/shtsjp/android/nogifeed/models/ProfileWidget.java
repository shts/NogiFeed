package shts.jp.android.nogifeed.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.UUID;

@ParseClassName("ProfileWidget")
public class ProfileWidget extends ParseObject {

    private static final String TAG = ProfileWidget.class.getSimpleName();

    public ProfileWidget() {}

    public static ProfileWidget getReference(String uuid) {
        try {
            return getQuery(uuid).getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ProfileWidget getReference(int widgetId) {
        try {
            return getQuery(widgetId).getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ProfileWidget> all() {
        try {
            return getQuery().find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean exist(String uuid) {
        return getReference(uuid) != null;
    }
    public static boolean exist(int widgetId) {
        return getReference(widgetId) != null;
    }

    public static void delete(String uuid) {
        ProfileWidget profileWidget = getReference(uuid);
        if (profileWidget == null) {
            return;
        }
        profileWidget.unpinInBackground();
    }
    public static void delete(int[] widgetIds) {
        for (int widgetId : widgetIds) {
            delete(widgetId);
        }
    }
    public static void delete(int widgetId) {
        ProfileWidget profileWidget = getReference(widgetId);
        if (profileWidget == null) {
            return;
        }
        profileWidget.unpinInBackground();
    }

    public static ParseQuery<ProfileWidget> getQuery(String uuid) {
        ParseQuery<ProfileWidget> query = ParseQuery.getQuery(ProfileWidget.class);
        query.whereEqualTo("uuid", uuid);
        query.fromLocalDatastore();
        return query;
    }
    public static ParseQuery<ProfileWidget> getQuery(int widgetId) {
        ParseQuery<ProfileWidget> query = ParseQuery.getQuery(ProfileWidget.class);
        query.whereEqualTo("widgetId", widgetId);
        query.fromLocalDatastore();
        return query;
    }

    public static ParseQuery<ProfileWidget> getQuery() {
        ParseQuery<ProfileWidget> query = ParseQuery.getQuery(ProfileWidget.class);
        query.fromLocalDatastore();
        return query;
    }

    public static void saveLocal(int widgetId, Member member) {
        ProfileWidget profileWidget = new ProfileWidget();
        profileWidget.put("widgetId", widgetId);
        profileWidget.put("memberObjectId", member.getObjectId());
        UUID uuid = UUID.randomUUID();
        profileWidget.put("uuid", uuid.toString());
        profileWidget.pinInBackground();
    }

    public String getUuid() {
        return getString("uuid");
    }

    public int getWidgetId() {
        return getInt("widgetId");
    }

    public String getMemberObjectId() {
        return getString("memberObjectId");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("uuid(").append(getUuid()).append(") ");
        sb.append("widgetId(").append(getWidgetId()).append(") ");
        sb.append("memberObjectId(").append(getMemberObjectId()).append(") ");
        return sb.toString();
    }
}
