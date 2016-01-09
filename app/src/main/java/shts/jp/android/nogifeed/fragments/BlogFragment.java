package shts.jp.android.nogifeed.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.api.ImageDownloadClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.DownloadFinishListener;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.utils.SdCardUtils;
import shts.jp.android.nogifeed.utils.SimpleImageDownloader;
import shts.jp.android.nogifeed.utils.WaitMinimunImageDownloader;
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

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
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
            Logger.e(TAG, "load beforeUrl : url(" + beforeUrl + ")");
            webView.loadUrl(beforeUrl);
            return view;
        }

        entry = Entry.getReference(getArguments().getString("entry"));
        entry.fetchIfNeededInBackground(new GetCallback<Entry>() {
            @Override
            public void done(Entry entry, ParseException e) {
                if (e != null || entry == null) {
                    Logger.e(TAG, "failed to get entry");
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
//                    DownloadFinishListener listener = new DownloadFinishListener(getActivity(), 1);
//                    ImageDownloadClient.get(getActivity(), url, listener);
                    download(url);
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
        List<String> urlList = new ArrayList<>();

        urlList.addAll(entry.getUploadedThumbnailUrlList());
        urlList.addAll(entry.getUploadedRawImageUrlList());

        if (urlList.isEmpty()) {
            Toast.makeText(getActivity(),
                    "画像がありません", Toast.LENGTH_SHORT).show();
            return false;
        }
        download(urlList);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("before-url", webView.getUrl());
        outState.putString("entry", entry.getObjectId());
        super.onSaveInstanceState(outState);
    }

    private List<String> downloadTargetList;
    private void download(List<String> urlList) {
        if (!hasPermission()) {
            // 権限がない場合はリクエスト
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_DOWNLOAD_ALL);
            downloadTargetList = urlList;
            return;
        }
        if (!new WaitMinimunImageDownloader(getActivity(), urlList).get()) {
            showSnackbar(false);
        }
    }

    private String downloadTarget;
    private void download(String url) {
        if (!hasPermission()) {
            // 権限がない場合はリクエスト
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_DOWNLOAD);
            downloadTarget = url;
            return;
        }
        if (!new SimpleImageDownloader(getActivity(), url).get()) {
            showSnackbar(false);
        }
    }

    private static final int REQUEST_DOWNLOAD = 1;
    private static final int REQUEST_DOWNLOAD_ALL = 2;

    private boolean hasPermission() {
        final int permission = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_DOWNLOAD == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download(downloadTarget);
            } else {
//                fabDownload.setColorNormalResId(R.color.accent);
//                fabDownload.setTitle("画像をダウンロードする");
//                Snackbar.make(coordinatorLayout, "アプリに書き込み権限がないためダウンロードできません。", Snackbar.LENGTH_LONG)
//                        .show();
                Toast.makeText(getActivity(), "アプリに書き込み権限がないためダウンロードできません", Toast.LENGTH_SHORT).show();
            }
        } else if (REQUEST_DOWNLOAD_ALL == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download(downloadTargetList);
            } else {
//                fabDownload.setColorNormalResId(R.color.accent);
//                fabDownload.setTitle("画像をダウンロードする");
//                Snackbar.make(coordinatorLayout, "アプリに書き込み権限がないためダウンロードできません。", Snackbar.LENGTH_LONG)
//                        .show();
                Toast.makeText(getActivity(), "アプリに書き込み権限がないためダウンロードできません", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri recentDownloadedUri;

    /**
     * WaitMinimunImageDownloader のコールバック
     * @param callback callback
     */
    @Subscribe
    public void onFinishDownload(
            WaitMinimunImageDownloader.Callback.ResponseDownloadImage callback) {
        if (callback != null && callback.file != null) {
            SdCardUtils.scanFile(getActivity(), callback.file,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.w(TAG, "path(" + path + ") uri(" + uri + ")");
                            recentDownloadedUri = uri;
                        }
                    });
        }
    }

    /**
     * WaitMinimunImageDownloader のコールバック
     * @param callback callback
     */
    @Subscribe
    public void onFinishDownload(
            WaitMinimunImageDownloader.Callback.CompleteDownloadImage callback) {
        // TODO: レスポンスリスト内にerrorがないことを確認してからSnackbarを表示すること
        // TODO: 一部画像のダウンロードに失敗した場合はその旨を通知すること
        showSnackbar(true);
    }

    /**
     * SimpleImageDownloader のコールバック
     * @param callback callback
     */
    @Subscribe
    public void onFinishDownload(SimpleImageDownloader.Callback callback) {
        if (callback != null && callback.file != null) {
            SdCardUtils.scanFile(getActivity(), callback.file,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.w(TAG, "path(" + path + ") uri(" + uri + ")");
                            recentDownloadedUri = uri;
                            final Activity activity = getActivity();
                            if (activity != null) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSnackbar(true);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    /**
     * Temp
     * @param success
     */
    private void showSnackbar(boolean success) {
        if (success) {
            Toast.makeText(getActivity(),
                    "成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(),
                    "失敗", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

}
