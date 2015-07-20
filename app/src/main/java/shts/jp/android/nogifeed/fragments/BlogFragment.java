package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.api.ThumbnailDownloadClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.views.dialogs.DownloadConfirmDialog;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

// TODO: How terrible code...
public class BlogFragment extends Fragment {

    private static final String TAG = BlogFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    //private MainActivity mActivity;
    private String mBlogUrl;
    private Entry mEntry;
    private WebView mWebView;
    private String mBeforeUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            mEntry = bundle.getParcelable(Entry.KEY);
            if (mEntry == null) {
                mBlogUrl = bundle.getString(BlogUpdateNotification.KEY);
            }
        } else {
            mBeforeUrl = savedInstanceState.getString(KEY_PAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, null);
        mWebView = (WebView) view.findViewById(R.id.browser);
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                WebView webView = (WebView) view;
                showDownloadConfirmDialog(webView);
                return false;
            }
        });

        mWebView.setWebViewClient(new BrowserViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        if (mEntry == null) {
            mWebView.loadUrl(mBlogUrl);
            return view;
        }

        if (mBeforeUrl == null) {
            mWebView.loadUrl(mEntry.link);
        } else {
            mWebView.loadUrl(mBeforeUrl);
            mBeforeUrl = null;
        }
        return view;
    }

    private void showDownloadConfirmDialog(WebView webView) {
        WebView.HitTestResult hr = webView.getHitTestResult();

        if (WebView.HitTestResult.IMAGE_TYPE == hr.getType()) {
            final String url = hr.getExtra();
            DownloadConfirmDialog confirmDialog = new DownloadConfirmDialog();
            confirmDialog.setCallbacks(new DownloadConfirmDialog.Callbakcs() {
                @Override
                public void onClickPositiveButton() {
                    ThumbnailDownloadClient.get(
                            getActivity(), url, mEntry, new DownloadFinishListener(getActivity(), 1));
                    // Toast.makeText(getActivity(), R.string.toast_download_complete, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onClickNegativeButton() {
                    // do nothing
                }
            });
            confirmDialog.show(getFragmentManager(), TAG);
        }
    }

    private class BrowserViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d(TAG, "shouldOverrideUrlLoading(WebView, String) in : url(" + url + ")");
            if (Uri.parse(url).getHost().equals("blog.nogizaka46.com")) {
                Logger.d(TAG, "shouldOverrideUrlLoading : " + true);
                return super.shouldOverrideUrlLoading(view, url);
            }
            Logger.d(TAG, "shouldOverrideUrlLoading : " + false);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.contains("smph")) {
                url = url.replace("smph/", "");
            }
            DataStoreUtils.readArticle(
                    getActivity().getApplicationContext(), url);
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mActivity = (MainActivity) activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PAGE_URL, mWebView.getUrl());
        super.onSaveInstanceState(outState);
    }

    public boolean goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

}
