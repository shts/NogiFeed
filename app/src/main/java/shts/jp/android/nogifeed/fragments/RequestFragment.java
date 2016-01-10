package shts.jp.android.nogifeed.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import shts.jp.android.nogifeed.R;

public class RequestFragment extends Fragment {

    private static final String TAG = RequestFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, null);

        final TextView tv = (TextView) view.findViewById(R.id.link);
        MovementMethod movementMethod = LinkMovementMethod.getInstance();
        tv.setMovementMethod(movementMethod);
        final String link = "お急ぎの場合、<a href=\"https://twitter.com/nogifeed\">Twitter @nogifeed </a>へリプライしていただくと返信が早いかもしれません";
        tv.setText(Html.fromHtml(link));

        final EditText editText = (EditText) view.findViewById(R.id.req_editor);

        final Button button = (Button) view.findViewById(R.id.req_send_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createSendRequestIntent(editText.getText().toString()));
            }
        });

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.nav_menu_request);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    private Intent createSendRequestIntent(String message) {
        return ShareCompat.IntentBuilder.from(getActivity())
                .addEmailTo("nogifeed@gmail.com")
                .setSubject("NogiFeed - フィードバック")
                .setText(getAppVersion() + message)
                .setType("text/plain")
                .getIntent();
    }

    private String getAppVersion() {
        final Context context = getActivity();
        String appPackageName = context.getPackageName();

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo p = pm.getPackageInfo(appPackageName, 0);

            String appVersionName = p.versionName;
            int appVersionCode = p.versionCode;

            final StringBuilder builder = new StringBuilder();
            builder.append("アプリ情報").append("\n");
            builder.append("=========================").append("\n");
            builder.append("メッセージ").append("\n");
            builder.append("appPackageName=").append(appPackageName).append("\n");
            builder.append("appVersion=").append(appVersionName)
                    .append(" (").append(appVersionCode).append(")\n");
            return builder.toString();

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("never reached", e);
        }
    }


}
