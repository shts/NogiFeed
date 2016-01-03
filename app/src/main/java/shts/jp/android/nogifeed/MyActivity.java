package shts.jp.android.nogifeed;

import android.os.Bundle;

import shts.jp.android.nogifeed.activities.MainActivity;
import shts.jp.android.nogifeed.activities.TopActivity;

public class MyActivity extends TopActivity {
//public class MyActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //((NogiFeedApplication) getApplication()).parseInstallation();
    }
}
