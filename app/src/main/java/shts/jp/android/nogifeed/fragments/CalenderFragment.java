package shts.jp.android.nogifeed.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import shts.jp.android.nogifeed.R;

public class CalenderFragment extends Fragment {

    private static final String TAG = CalenderFragment.class.getSimpleName();

    public static CalenderFragment newInstance() {
        return new CalenderFragment();
    }

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, null);
        webView = (WebView) view.findViewById(R.id.browser);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals("sp.nogizaka46.com")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        webView.loadUrl("http://sp.nogizaka46.com/q?i=calendar/index&ymd");
        return view;
    }

    public boolean goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
