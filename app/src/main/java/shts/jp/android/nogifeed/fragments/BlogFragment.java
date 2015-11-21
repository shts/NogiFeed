package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.parse.GetCallback;
import com.parse.ParseException;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.views.dialogs.DownloadConfirmDialog;

// TODO: How terrible code...
// TODO:
public class BlogFragment extends Fragment {

    private static final String TAG = BlogFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    private WebView mWebView;
    private String mBeforeUrl;

    private String entryObjectId;
    private Entry entry;

    public static BlogFragment newBlogFragment(String entryObjectId) {
        Bundle bundle = new Bundle();
        bundle.putString(Entry.KEY, entryObjectId);
        BlogFragment blogFragment = new BlogFragment();
        blogFragment.setArguments(bundle);
        return blogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            entryObjectId = bundle.getString(Entry.KEY);
            entry = Entry.getReference(entryObjectId);
            //mBlogUrl = bundle.getString(BlogUpdateNotification.KEY);
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
        //mWebView.addJavascriptInterface(new GetHtmlTextInterface(), "HTMLOUT");

//        if (mBlogEntry == null) {
//            mWebView.loadUrl(mBlogUrl);
//            return view;
//        }

//        if (mBeforeUrl == null) {
//            mWebView.loadUrl(mBlogEntry.url);
//
//        } else {
//            mWebView.loadUrl(mBeforeUrl);
//            mBeforeUrl = null;
//        }

        entry.fetchIfNeededInBackground(new GetCallback<Entry>() {
            @Override
            public void done(Entry entry, ParseException e) {
                if (e != null || entry == null) {
                    Logger.e(TAG, "cannot fetch entry", e);
                    return;
                }
                Logger.v(TAG, "fetch done entry(" + entry.toString() + ")");
                mWebView.loadUrl(entry.getBlogUrl());
            }
        });

        setHasOptionsMenu(true);

        return view;
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
                    // TODO: download image
//                    if (mBlogEntry == null) {
//                        Toast.makeText(getActivity(), R.string.toast_failed_download, Toast.LENGTH_SHORT).show();
//                        return;
//                    } else {
//                        final Entry entry = mBlogEntry.toEntryObject();
//                        entry.content = mContent;
//                        ThumbnailDownloadClient.get(
//                                getActivity(), url, entry, new DownloadFinishListener(getActivity(), 1));
//                    }
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
            /* This call inject JavaScript into the page which just finished loading. */
            mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            NotYetRead.delete(entryObjectId);
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
        // TODO: download url list
        entry.getUploadedThumbnailUrlList();
//        if (mBlogEntry == null || TextUtils.isEmpty(mBlogEntry.content)) {
//            if (TextUtils.isEmpty(mContent)) {
//                Toast.makeText(activity.getApplicationContext(),
//                        R.string.toast_failed_download, Toast.LENGTH_SHORT).show();
//                return super.onOptionsItemSelected(item);
//            }
//        }
//        if (mBlogEntry != null) {
//            final Entry entry = mBlogEntry.toEntryObject();
//            entry.content = mContent;
//            ImageDownloader.downloads(activity.getApplicationContext(), entry);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PAGE_URL, mWebView.getUrl());
//        outState.putParcelable(BlogEntry.KEY, mBlogEntry);
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
