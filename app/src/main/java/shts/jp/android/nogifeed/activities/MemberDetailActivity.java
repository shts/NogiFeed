package shts.jp.android.nogifeed.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.fragments.MemberDetailFragment2;
import shts.jp.android.nogifeed.models.Member;

public class MemberDetailActivity extends AppCompatActivity {

    private static final String TAG = MemberDetailActivity.class.getSimpleName();

    public static Intent getStartIntent(Context context, Member member) {
        return getStartIntent(context, member.getObjectId());
    }

    public static Intent getStartIntent(Context context, String memberObjectId) {
        Intent intent = new Intent(context, MemberDetailActivity.class);
        intent.putExtra("memberObjectId", memberObjectId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail2);

        String memberObjectId = getIntent().getStringExtra("memberObjectId");
        MemberDetailFragment2 memberDetailFragment2
                = MemberDetailFragment2.newInstance(memberObjectId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, memberDetailFragment2, MemberDetailFragment2.class.getSimpleName());
        ft.commit();
    }

}
