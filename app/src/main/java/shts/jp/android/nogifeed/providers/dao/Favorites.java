package shts.jp.android.nogifeed.providers.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.providers.NogiFeedContent;

public class Favorites extends ArrayList<Favorite> {

    @NonNull
    public static Favorites all(@NonNull Context context) {
        Favorites favorites = new Favorites();
        Cursor c = context.getContentResolver().query(
                NogiFeedContent.Favorite.CONTENT_URI, null, null, null, null);
        if (c == null || !c.moveToFirst()) return favorites;
        try {
            do {
                int id = c.getInt(c.getColumnIndex(NogiFeedContent.Favorite.Key.ID));
                int memberId = c.getInt(c.getColumnIndex(NogiFeedContent.Favorite.Key.MEMBER_ID));
                favorites.add(new Favorite(id, memberId));
            } while (c.moveToNext());
        } finally {
            c.close();
        }
        return favorites;
    }

    /**
     * 指定したメンバーのお気に入り状況をトグルする
     *
     * @param context
     * @param member
     * @return トグルした結果を返却する。trueなら推しメン登録, falseなら推しメン解除
     */
    public static void toggle(@NonNull Context context, @NonNull Member member) {
        toggle(context, member.getId());
    }

    public static void toggle(@NonNull Context context, int memberId) {
        if (exist(context, memberId)) {
            remove(context, memberId);
        } else {
            add(context, memberId);
        }
    }

    private static void add(@NonNull Context context, int memberId) {
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.Favorite.Key.MEMBER_ID, memberId);
        context.getContentResolver().insert(NogiFeedContent.Favorite.CONTENT_URI, cv);

        final Scheduler scheduler = Schedulers.newThread();
        NogiFeedApiClient.addFavorite(memberId)
                .subscribeOn(scheduler)
                .observeOn(scheduler)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                });
    }

    private static void remove(@NonNull Context context, int memberId) {
        String selection = NogiFeedContent.Favorite.Key.MEMBER_ID + "=?";
        String[] selectionArgs = {String.valueOf(memberId)};
        context.getContentResolver().delete(NogiFeedContent.Favorite.CONTENT_URI, selection, selectionArgs);

        final Scheduler scheduler = Schedulers.newThread();
        NogiFeedApiClient.removeFavorite(memberId)
                .subscribeOn(scheduler)
                .observeOn(scheduler)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                });
    }

    public static boolean exist(@NonNull Context context, @NonNull Member member) {
        return exist(context, member.getId());
    }

    public static boolean exist(@NonNull Context context, int memberId) {
        String selection = NogiFeedContent.Favorite.Key.MEMBER_ID + "=?";
        String[] selectionArgs = {String.valueOf(memberId)};

        Cursor c = context.getContentResolver().query(
                NogiFeedContent.Favorite.CONTENT_URI,
                NogiFeedContent.Favorite.sProjection,
                selection, selectionArgs, null);
        if (c == null || !c.moveToFirst()) return false;
        try {
            return c.getCount() == 1;
        } finally {
            c.close();
        }
    }

}
