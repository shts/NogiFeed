package shts.jp.android.nogifeed.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.AllMemberGridListFragment;

public class AllMemberActivity extends AppCompatActivity {

    private static final String TAG = AllMemberActivity.class.getSimpleName();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, AllMemberActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_member);

        AllMemberGridListFragment allMemberGridListFragment
                = AllMemberGridListFragment.newInstance(AllMemberGridListFragment.Type.ADD_FAVORITE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, allMemberGridListFragment, AllMemberActivity.class.getSimpleName());
        ft.commit();
    }
}
