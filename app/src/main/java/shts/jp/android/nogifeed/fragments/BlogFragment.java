package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.api.ThumbnailDownloadClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.BlogEntry;
import shts.jp.android.nogifeed.entities.Entry;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.models.UnRead;
import shts.jp.android.nogifeed.services.ImageDownloader;
import shts.jp.android.nogifeed.views.dialogs.DownloadConfirmDialog;
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification;

// TODO: How terrible code...
public class BlogFragment extends Fragment {

    private static final String TAG = BlogFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    private String mBlogUrl;
    private BlogEntry mBlogEntry;
    private WebView mWebView;
    private String mBeforeUrl;
    private String mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            mBlogEntry = bundle.getParcelable(BlogEntry.KEY);
            mBlogUrl = bundle.getString(BlogUpdateNotification.KEY);
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
        mWebView.addJavascriptInterface(new GetHtmlTextInterface(), "HTMLOUT");

        if (mBlogEntry == null) {
            mWebView.loadUrl(mBlogUrl);
            return view;
        }

        if (mBeforeUrl == null) {
            mWebView.loadUrl(mBlogEntry.url);

        } else {
            mWebView.loadUrl(mBeforeUrl);
            mBeforeUrl = null;
        }

        setHasOptionsMenu(true);

        return view;
    }

    class GetHtmlTextInterface {
        @JavascriptInterface
        public void processHTML(String html) {
            mContent = html;
        }
    }

    private void showDownloadConfirmDialog(WebView webView) {
        WebView.HitTestResult hr = webView.getHitTestResult();
        Logger.v(TAG, "showDownloadConfirmDialog() in : hr type(" + hr.getType() + ")");

        if ((WebView.HitTestResult.IMAGE_TYPE == hr.getType())
                || (WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE == hr.getType())) {
            final String url = hr.getExtra();
            Logger.v(TAG, "showDownloadConfirmDialog() in : url(" + url + ")");
            DownloadConfirmDialog confirmDialog = new DownloadConfirmDialog();
            confirmDialog.setCallbacks(new DownloadConfirmDialog.Callbacks() {
                @Override
                public void onClickPositiveButton() {
                    if (mBlogEntry == null) {
                        Toast.makeText(getActivity(), R.string.toast_failed_download, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        final Entry entry = mBlogEntry.toEntryObject();
                        entry.content = mContent;
                        ThumbnailDownloadClient.get(
                                getActivity(), url, entry, new DownloadFinishListener(getActivity(), 1));
                    }
                }
                @Override
                public void onClickNegativeButton() {}
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
            markReadArticle(url);
            /* This call inject JavaScript into the page which just finished loading. */
            mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
        }

        private void markReadArticle(String url) {
            if (url.contains("smph")) {
                url = url.replace("smph/", "");
            }
            final Activity activity = getActivity();
            if (activity != null) {
                UnRead.readComplete(
                        getActivity().getApplicationContext(), url);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_blog, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Activity activity = getActivity();
        if (activity == null) {
            Logger.w(TAG, "cannot show Menu because activity is null");
            return false;
        }
        if (mBlogEntry == null || TextUtils.isEmpty(mBlogEntry.content)) {
            if (TextUtils.isEmpty(mContent)) {
                Toast.makeText(activity.getApplicationContext(),
                        R.string.toast_failed_download, Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
        }
        if (mBlogEntry != null) {
            final Entry entry = mBlogEntry.toEntryObject();
            entry.content = mContent;
            ImageDownloader.downloads(activity.getApplicationContext(), entry);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PAGE_URL, mWebView.getUrl());
        outState.putParcelable(BlogEntry.KEY, mBlogEntry);
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
