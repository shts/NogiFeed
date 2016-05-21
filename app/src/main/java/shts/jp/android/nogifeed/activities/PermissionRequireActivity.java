package shts.jp.android.nogifeed.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class PermissionRequireActivity extends AppCompatActivity {

    private static final String TAG = PermissionRequireActivity.class.getSimpleName();

    @IntDef(Type.DOWNLOAD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int DOWNLOAD = 0;
    }

    @StringDef(ExtraKey.DOWNLOAD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExtraKey {
        String TYPE = "type";
        String DOWNLOAD = "urlList";
    }

    @State
    ArrayList<String> urlList;

    public static Intent getDownloadStartIntent(@NonNull Context context, @NonNull String url) {
        ArrayList<String> urlList = new ArrayList<>();
        urlList.add(url);
        return getDownloadStartIntent(context, urlList);
    }

    public static Intent getDownloadStartIntent(@NonNull Context context, @NonNull ArrayList<String> urlList) {
        Intent intent = new Intent(context, PermissionRequireActivity.class);
        intent.putExtra(ExtraKey.TYPE, Type.DOWNLOAD);
        intent.putStringArrayListExtra(ExtraKey.DOWNLOAD, urlList);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        } else {
            urlList = getIntent().getStringArrayListExtra(ExtraKey.DOWNLOAD);
        }
        PermissionRequireActivityPermissionsDispatcher.requestWithCheck(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void request() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ExtraKey.DOWNLOAD, urlList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onStoragePermissionDenied() {
        finish();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationalForStrage(final PermissionRequest request) {
        showRationalDialog(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 1つでも許可されないパーミッションがあった場合はActivityを終了させる
        if (!PermissionUtils.verifyPermissions(grantResults)) {
            finish();
        }
        PermissionRequireActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void showRationalDialog(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                //.setMessage(R.string.permission_rationale_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
