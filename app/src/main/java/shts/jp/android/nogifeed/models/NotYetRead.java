package shts.jp.android.nogifeed.models;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import shts.jp.android.nogifeed.entities.Blog;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;

@ParseClassName("NotYetRead")
public class NotYetRead extends ParseObject {

    private static final String TAG = NotYetRead.class.getSimpleName();

    public static ParseQuery<NotYetRead> getQuery(Member member) {
        ParseQuery<NotYetRead> query = ParseQuery.getQuery(NotYetRead.class);
        query.whereEqualTo("memberObjectId", member.getObjectId());
        query.fromLocalDatastore();
        return query;
    }
    public static ParseQuery<NotYetRead> getQuery(ProfileWidget profileWidget) {
        ParseQuery<NotYetRead> query = ParseQuery.getQuery(NotYetRead.class);
        query.whereEqualTo("memberObjectId", profileWidget.getMemberObjectId());
        query.fromLocalDatastore();
        return query;
    }
    public static ParseQuery<NotYetRead> getQuery(String articleUrl) {
        ParseQuery<NotYetRead> query = ParseQuery.getQuery(NotYetRead.class);
        query.whereEqualTo("articleUrl", articleUrl);
        query.fromLocalDatastore();
        return query;
    }

    /**
     * 推しメン登録状態変更通知
     */
    public static class ChangedReadState {}

    public static boolean isRead(String articleUrl) {
        try {
            List<NotYetRead> notYetRead = getQuery(articleUrl).find();
            return (notYetRead != null) && !notYetRead.isEmpty();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int count(ProfileWidget profileWidget) {
        try {
            return getQuery(profileWidget).count();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int count(Member member) {
        try {
            return getQuery(member).count();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void add(Blog blog) {
        NotYetRead notYetRead = new NotYetRead();
        notYetRead.put("articleUrl", blog.getUrl());
        notYetRead.put("memberObjectId", blog.getAuthorId());
        notYetRead.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                BusHolder.get().post(new ChangedReadState());
            }
        });
    }

    public static void delete(String articleUrl) {
        getQuery(articleUrl).getFirstInBackground(new GetCallback<NotYetRead>() {
            @Override
            public void done(NotYetRead notYetRead, ParseException e) {
                if (e != null || notYetRead == null) {
                    return;
                }
                notYetRead.unpinInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        BusHolder.get().post(new ChangedReadState());
                    }
                });
            }
        });
    }

}
