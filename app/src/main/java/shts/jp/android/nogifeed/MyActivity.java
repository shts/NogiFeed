package shts.jp.android.nogifeed;

import android.os.Bundle;
import android.util.Log;

import shts.jp.android.nogifeed.activities.MainActivity;
import shts.jp.android.nogifeed.models.UnRead;

public class MyActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NogiFeedApplication) getApplication()).parseInstallation();
    }
}
