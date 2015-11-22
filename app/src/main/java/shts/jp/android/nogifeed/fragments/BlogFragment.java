package shts.jp.android.nogifeed.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.api.ImageDownloadClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.views.dialogs.DownloadConfirmDialog;

// TODO: How terrible code...
// TODO:
public class BlogFragment extends Fragment {

    private static final String TAG = BlogFragment.class.getSimpleName();
    private static final String KEY_PAGE_URL = "key_page_url";

    private WebView mWebView;
    private String mBeforeUrl = null;

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

    @SuppressLint("SetJavaScriptEnabled")
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

        if (!TextUtils.isEmpty(mBeforeUrl)) {
            Logger.v(TAG, "load before url : url(" + mBeforeUrl + ")");
            mWebView.loadUrl(mBeforeUrl);
            return view;
        }

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
                    DownloadFinishListener listener = new DownloadFinishListener(getActivity(), 1);
                    ImageDownloadClient.get(getActivity(), url, listener);
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
        final Context context = getActivity().getApplicationContext();

        final List<String> target = new ArrayList<>();
        final List<String> thumbnailUrls = entry.getUploadedThumbnailUrlList();
        if (thumbnailUrls != null && !thumbnailUrls.isEmpty()) {
            target.addAll(thumbnailUrls);
        }
        final List<String> rawImageUrls = entry.getUploadedRawImageUrlList();
        if (rawImageUrls != null && !rawImageUrls.isEmpty()) {
            target.addAll(rawImageUrls);
        }
        if (target.size() <= 0) {
            Toast.makeText(context, R.string.toast_failed_download, Toast.LENGTH_SHORT).show();
            return false;
        }
        ImageDownloadClient.get(context, target,
                new DownloadFinishListener(context, target.size()));
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
