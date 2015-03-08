package shts.jp.android.nogifeed;

import android.os.Bundle;

public class MyActivity extends shts.jp.android.nogifeed.activities.MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NogiFeedApplication) getApplication()).parseInstllation();
    }
}
