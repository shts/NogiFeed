package shts.jp.android.nogifeed.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.Banner;
import shts.jp.android.nogifeed.entities.News;

public class NewsBrowseFragment extends Fragment {

    private static final String TAG = NewsBrowseFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    private WebView mWebView;
    private String mBeforeUrl;
    private News mNews;
    private Banner mBanner;

    private final WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.d(TAG, "url(" + url + ")");
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
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            mNews = bundle.getParcelable(News.KEY);
            mBanner = bundle.getParcelable(Banner.KEY);
        } else {
            mBeforeUrl = savedInstanceState.getString(KEY_PAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, null);
        mWebView = (WebView) view.findViewById(R.id.browser);
        mWebView.setWebViewClient(mWebViewClient);

        if (mBeforeUrl != null) {
            mWebView.loadUrl(mBeforeUrl);
        } else {
            if (mNews != null) {
                mWebView.loadUrl(mNews.url);
            } else {
                Logger.d(TAG, "url(" + mBanner.linkurl + ")");
                if (!mBanner.linkurl.startsWith("http")) {
                    if (mBanner.linkurl.startsWith(".")) {
                        mBanner.linkurl = mBanner.linkurl.replace(".", "http://www.nogizaka46.com/");
                    }
                }
                mWebView.loadUrl(mBanner.linkurl);
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Banner.KEY, mBanner);
        outState.putParcelable(News.KEY, mNews);
        outState.putString(KEY_PAGE_URL, mWebView.getUrl());
        super.onSaveInstanceState(outState);
    }
}
