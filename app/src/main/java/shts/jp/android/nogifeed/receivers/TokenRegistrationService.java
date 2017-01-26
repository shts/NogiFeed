package shts.jp.android.nogifeed.receivers;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;

/**
 * FCMのトークンを登録するサービス
 */
public class TokenRegistrationService extends IntentService {

    private static final String TAG = TokenRegistrationService.class.getSimpleName();

    public TokenRegistrationService() {
        super(TokenRegistrationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: in");
        final Context context = getApplicationContext();
        createGetTokenObservable()
                .flatMap(new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(final String regId) {
                        // getToken()で取得した値が空の場合
                        if (TextUtils.isEmpty(regId)) {
                            return null;
                        }

                        String oldRegId = Store.getRegId(context);
                        // Idが更新された場合は既存のIDを上書き登録する
                        if (!regId.equals(oldRegId)) {
                            Store.setRegId(context, regId);
                            return NogiFeedApiClient.registrationId(regId);
                        }

                        // 旧サーバーから新サーバーにトークンを送信する
                        if (Store.isLegacy(context)) {
                            Store.removeLegacy(context);
                            return NogiFeedApiClient.registrationId(oldRegId);
                        }

                        return null;
                    }
                })
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
        Log.d(TAG, "onHandleIntent: out");
    }

    /**
     * FCMのトークンを取得する
     * <p>
     * 初回起動時にnullの場合があるのでリトライする
     *
     * @return FCMのトークン
     */
    private static Observable<String> createGetTokenObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String regId = FirebaseInstanceId.getInstance().getToken();
                    Log.d(TAG, "call: regId(" + regId + ")");
                    // null の場合があるのでリトライするためExceptionをなげる
                    if (TextUtils.isEmpty(regId)) {
                        throw new Throwable();
                    }
                    subscriber.onNext(regId);
                    subscriber.onCompleted();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }
            }
        }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(final Observable<? extends Throwable> observable) {
                return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        Log.d(TAG, "call: Retry get token");
                        // 3秒後にリトライする
                        return Observable.timer(3, TimeUnit.SECONDS);
                    }
                });
            }
        });
    }

    private static class Store {

        private static final String IS_LEGACY = "is_legacy";
        private static final String REG_PREF = "reg_pref";
        private static final String REG_ID = "reg_id";

        private static SharedPreferences getPref(@NonNull Context context) {
            return context.getSharedPreferences(REG_PREF, Context.MODE_PRIVATE);
        }

        private static boolean isLegacy(@NonNull Context context) {
            return getPref(context).getBoolean(IS_LEGACY, true);
        }

        @SuppressLint("CommitPrefEdits")
        private static void removeLegacy(@NonNull Context context) {
            getPref(context).edit().putBoolean(IS_LEGACY, false).commit();
        }

        @SuppressLint("CommitPrefEdits")
        private static void setRegId(@NonNull Context context, @NonNull String regId) {
            getPref(context).edit().putString(REG_ID, regId).commit();
        }

        @Nullable
        private static String getRegId(@NonNull Context context) {
            return getPref(context).getString(REG_ID, null);
        }
    }
}