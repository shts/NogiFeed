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
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.views.dialogs.DownloadConfirmDialog;

// TODO: How terrible code...
// TODO:
public class BlogFragment extends Fragment {

    private static final String TAG = BlogFragment.class.getSimpleName();

    private WebView webView;
    private String beforeUrl = null;
    private Entry entry;

    public static BlogFragment newInstance(String entryObjectId) {
        Bundle bundle = new Bundle();
        bundle.putString("entry", entryObjectId);
        BlogFragment blogFragment = new BlogFragment();
        blogFragment.setArguments(bundle);
        return blogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            beforeUrl = savedInstanceState.getString("before-url");
            entry = Entry.getReference(getArguments().getString("entry"));
            entry.fetchIfNeededInBackground();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, null);
        webView = (WebView) view.findViewById(R.id.browser);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                WebView webView = (WebView) view;
                showDownloadConfirmDialog(webView);
                return false;
            }
        });
        webView.setWebViewClient(new BrowserViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        setHasOptionsMenu(true);

        if (!TextUtils.isEmpty(beforeUrl)) {
            webView.loadUrl(beforeUrl);
            return view;
        }

        entry = Entry.getReference(getArguments().getString("entry"));
        entry.fetchIfNeededInBackground(new GetCallback<Entry>() {
            @Override
            public void done(Entry entry, ParseException e) {
                if (e != null || entry == null) {
                    return;
                }
                webView.loadUrl(entry.getBlogUrl());
            }
        });
        return view;
    }

    private void showDownloadConfirmDialog(WebView webView) {
        WebView.HitTestResult hr = webView.getHitTestResult();

        if ((WebView.HitTestResult.IMAGE_TYPE == hr.getType())
                || (WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE == hr.getType())) {
            final String url = hr.getExtra();
            DownloadConfirmDialog confirmDialog = new DownloadConfirmDialog();
            confirmDialog.setCallbacks(new DownloadConfirmDialog.Callbacks() {
                @Override
                public void onClickPositiveButton() {
                    // TODO: use new Downloader
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
            if (Uri.parse(url).getHost().equals("blog.nogizaka46.com")) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            NotYetRead.delete(entry.getObjectId());
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
        outState.putString("before-url", webView.getUrl());
        outState.putString("entry", entry.getObjectId());
        super.onSaveInstanceState(outState);
    }

    public boolean goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

}
