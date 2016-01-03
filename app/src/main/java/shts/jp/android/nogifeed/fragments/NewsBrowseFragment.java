package shts.jp.android.nogifeed.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.entities.News;
import shts.jp.android.nogifeed.utils.ShareUtils;

public class NewsBrowseFragment extends Fragment {

    private static final String TAG = NewsBrowseFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    private WebView webView;
    private String beforeUrl;
    private News news;

    public static NewsBrowseFragment newInstance(News news) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("news", news);
        NewsBrowseFragment newsBrowseFragment = new NewsBrowseFragment();
        newsBrowseFragment.setArguments(bundle);
        return newsBrowseFragment;
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!url.startsWith("http")) {
                if (url.startsWith(".")) {
                    url = url.replace(".", "http://www.nogizaka46.com/");
                }
            }
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        news = getArguments().getParcelable("news");
        if (savedInstanceState != null) {
            beforeUrl = savedInstanceState.getString(KEY_PAGE_URL);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_browse, null);
        webView = (WebView) view.findViewById(R.id.browser);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);

        if (!TextUtils.isEmpty(beforeUrl)) {
            webView.loadUrl(beforeUrl);
        } else {
            webView.loadUrl(news.url);
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(news.title);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_18dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ShareUtils.getNewsFeedIntent(news));
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("news", news);
        outState.putString(KEY_PAGE_URL, webView.getUrl());
        super.onSaveInstanceState(outState);
    }
}
