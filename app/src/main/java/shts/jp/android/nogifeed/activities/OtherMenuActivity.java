package shts.jp.android.nogifeed.activities;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.AboutFragment;
import shts.jp.android.nogifeed.fragments.LisencesFragment;
import shts.jp.android.nogifeed.fragments.RequestFragment;

/**
 * ドロワーのそのほかメニューから遷移する画面
 */
public class OtherMenuActivity extends AppCompatActivity {

    private static final String TAG = OtherMenuActivity.class.getSimpleName();

    public static Intent getStartIntent(Context context, int id) {
        Intent intent = new Intent(context, OtherMenuActivity.class);
        intent.putExtra("menu_id", id);
        return intent;
    }

    private final Fragment getFragmentFrom(int id) {
        switch (id) {
            case R.id.menu_lisences : return new LisencesFragment();
            case R.id.menu_request : return new RequestFragment();
            case R.id.menu_about_app : return new AboutFragment();
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        Fragment fragment = getFragmentFrom(getIntent().getIntExtra("menu_id", -1));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, OtherMenuActivity.class.getSimpleName());
        ft.commit();
    }
}
