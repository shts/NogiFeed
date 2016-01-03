package shts.jp.android.nogifeed.models;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;

@ParseClassName("Favorite")
public class Favorite extends ParseObject {
    // http://qiita.com/ryugoo/items/b3a508486b299b0deac7
    private static final String TAG = Favorite.class.getSimpleName();

    public Favorite() {
        // for ParseObject
    }

    public static ParseQuery<Favorite> getQuery() {
        ParseQuery<Favorite> query = ParseQuery.getQuery(Favorite.class);
        query.fromLocalDatastore();
        return query;
    }

    public static ParseQuery<Favorite> getQuery(String memberObjectId) {
        ParseQuery<Favorite> query = ParseQuery.getQuery(Favorite.class);
        query.whereEqualTo("memberObjectId", memberObjectId);
        query.fromLocalDatastore();
        return query;
    }

    /**
     * 推しメン全件を取得するコールバック
     */
    public static class GetFavoritesCallback {
        public List<Favorite> favorites;
        public ParseException e;
        public GetFavoritesCallback(List<Favorite> favorites, ParseException e) {
            this.favorites = favorites;
            this.e = e;
        }
        public boolean hasError() {
            return this.e != null || this.favorites == null || this.favorites.isEmpty();
        }
    }

    /**
     * 推しメン全件を取得する
     */
    public static void all() {
        getQuery().findInBackground(new FindCallback<Favorite>() {
            @Override
            public void done(List<Favorite> favorites, ParseException e) {
                BusHolder.get().post(new GetFavoritesCallback(favorites, e));
            }
        });
    }

    public static void toggle(String memberObjectId) {
        if (exist(memberObjectId)) {
            removeMember(memberObjectId);
        } else {
            add(memberObjectId);
        }
    }

    public static boolean exist(String memberObjectId) {
        try {
            List<Favorite> favorites = getQuery(memberObjectId).find();
            return (favorites != null) && (!favorites.isEmpty());
        } catch (ParseException e) {
            Logger.e(TAG, "cannot get favorite", e);
        }
        return false;
    }

    /**
     * 推しメン登録状態変更通知
     */
    public static class ChangedFavoriteState {
        public enum Action { ADD, REMOVE }
        public final Action action;
        public final ParseException e;
        public ChangedFavoriteState(Action action, ParseException e) {
            this.action = action;
            this.e = e;
        }
    }

    public static void add(List<Member> memberList) {
        for (Member member : memberList) {
            add(member);
        }
    }

    public static void add(String memberObjectId) {
        Favorite fav = new Favorite();
        fav.put("memberObjectId", memberObjectId);
        fav.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                BusHolder.get().post(new ChangedFavoriteState(ChangedFavoriteState.Action.ADD, e));
            }
        });
    }

    public static void add(Member member) {
        add(member.getObjectId());
    }

    public static void removeMember(String memberObjectId) {
        try {
            Favorite favorite = getQuery(memberObjectId).getFirst();
            if (favorite != null) {
                Logger.v(TAG, "favorite");
                favorite.unpinInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        BusHolder.get().post(new ChangedFavoriteState(ChangedFavoriteState.Action.REMOVE, e));
                    }
                });
            } else {
                Logger.e(TAG, "cannot get favorite member");
            }
        } catch (ParseException e) {
            Logger.e(TAG, "cannot get favorite member", e);
            BusHolder.get().post(new ChangedFavoriteState(ChangedFavoriteState.Action.REMOVE, e));
        }
    }

    public String getMemberObjectId() {
        return getString("memberObjectId");
    }
}
