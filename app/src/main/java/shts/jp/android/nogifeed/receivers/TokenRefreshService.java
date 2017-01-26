package shts.jp.android.nogifeed.receivers;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class TokenRefreshService extends FirebaseInstanceIdService {

    private static final String TAG = TokenRefreshService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh: ");
        startService(new Intent(this, TokenRegistrationService.class));
    }
}