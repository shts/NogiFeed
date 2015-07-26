package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.models.News;

public class NewsBrowseFragment extends Fragment {

    private static final String TAG = NewsBrowseFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    private WebView mWebView;
    private String mBeforeUrl;
    private News mNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            mNews = bundle.getParcelable(News.KEY);
        } else {
            mBeforeUrl = savedInstanceState.getString(KEY_PAGE_URL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, null);
        mWebView = (WebView) view.findViewById(R.id.browser);

        if (mBeforeUrl != null) {
            mWebView.loadUrl(mBeforeUrl);
        } else {
            mWebView.loadUrl(mNews.url);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(News.KEY, mNews);
        outState.putString(KEY_PAGE_URL, mWebView.getUrl());
        super.onSaveInstanceState(outState);
    }
}
