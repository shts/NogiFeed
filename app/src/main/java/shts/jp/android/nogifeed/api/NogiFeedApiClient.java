package shts.jp.android.nogifeed.api;

import android.support.annotation.CheckResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import shts.jp.android.nogifeed.BuildConfig;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.Members;

public class NogiFeedApiClient {

    private static NogiFeedApiService apiService;

    @CheckResult
    public static Observable<Members> getAllMembers() {
        return getApiService().getAllMembers();
    }

    @CheckResult
    public static Observable<Member> getMember(int id) {
        return getApiService().getMember(id);
    }

    @CheckResult
    public static Observable<Entries> getAllEntries(int skip, int limit) {
        return getApiService().getAllEntries(skip, limit);
    }

    @CheckResult
    public static Observable<Entries> getMemberEntries(int memberId, int skip, int limit) {
        List<Integer> ids = new ArrayList<>();
        ids.add(memberId);
        return getApiService().getMemberEntries(ids, skip, limit);
    }

    @CheckResult
    public static Observable<Entries> getMemberEntries(List<Integer> memberIds, int skip, int limit) {
        return getApiService().getMemberEntries(memberIds, skip, limit);
    }

    @CheckResult
    public static Observable<Void> addFavorite(int memberId) {
        return getApiService().changeFavoriteState(createFavoritePostBody(memberId, "incriment"));
    }

    @CheckResult
    public static Observable<Void> removeFavorite(int memberId) {
        return getApiService().changeFavoriteState(createFavoritePostBody(memberId, "decriment"));
    }

    @CheckResult
    public static Observable<Void> registrationId(String regId) {
        return getApiService().registrationId(createRegistrationPostBody(regId));
    }

    private static HashMap<String, String> createRegistrationPostBody(String regId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("reg_id", regId);
        return hashMap;
    }

    private static HashMap<String, String> createFavoritePostBody(int memberId, String action) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("member_id", String.valueOf(memberId));
        hashMap.put("action", action);
        return hashMap;
    }

    private static synchronized NogiFeedApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient())
                    .baseUrl(BuildConfig.BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(NogiFeedApiService.class);
        }
        return apiService;
    }

    private static OkHttpClient createOkHttpClient() {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            return new OkHttpClient.Builder()
                    .addNetworkInterceptor(logging)
                    .build();
        } else {
            return new OkHttpClient.Builder().build();
        }
    }

    private interface NogiFeedApiService {

        @GET("/members")
        Observable<Members> getAllMembers();

        @GET("/entries")
        Observable<Entries> getAllEntries(@Query("skip") int skip, @Query("limit") int limit);

        @GET("/members/{id}")
        Observable<Member> getMember(@Path("id") int id);

        @GET("/member/entries")
        Observable<Entries> getMemberEntries(
                @Query("ids[]") List<Integer> ids, @Query("skip") int skip, @Query("limit") int limit);

        @Headers({
                "Accept: application/json",
                "Content-type: application/json"
        })
        @POST("/favorite")
        Observable<Void> changeFavoriteState(@Body HashMap<String, String> body);

        @Headers({
                "Accept: application/json",
                "Content-type: application/json"
        })
        @POST("/registration")
        Observable<Void> registrationId(@Body HashMap<String, String> body);

    }
}
