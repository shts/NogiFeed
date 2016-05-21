package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.otto.Subscribe;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.ArrayList;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.PermissionRequireActivity;
import shts.jp.android.nogifeed.entities.Blog;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.utils.SdCardUtils;
import shts.jp.android.nogifeed.utils.ShareUtils;
import shts.jp.android.nogifeed.utils.SimpleImageDownloader;
import shts.jp.android.nogifeed.utils.WaitMinimunImageDownloader;
import shts.jp.android.nogifeed.views.dialogs.DownloadConfirmDialog;

public class BlogFragment extends Fragment {

    private static final String TAG = BlogFragment.class.getSimpleName();

    private static final int DOWNLOAD = 0;
    private static final int DOWNLOAD_LIST = 1;

    private WebView webView;

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fabDownload;
    private FloatingActionsMenu floatingActionsMenu;

    private Blog blog;

    public static BlogFragment newInstance(Blog blog) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("blog", blog);
        BlogFragment blogFragment = new BlogFragment();
        blogFragment.setArguments(bundle);
        return blogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            blog = savedInstanceState.getParcelable("blog");
        } else {
            blog = getArguments().getParcelable("blog");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("blog", blog);
        super.onSaveInstanceState(outState);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, null);

        floatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        FloatingActionButton fabShare = (FloatingActionButton) view.findViewById(R.id.fab_action_share);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                startActivity(ShareUtils.getShareBlogIntent(blog));
            }
        });
        fabDownload = (FloatingActionButton) view.findViewById(R.id.fab_action_download);
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urlList = new ArrayList<>();
                urlList.addAll(blog.getUploadedThumbnailUrlList());
                urlList.addAll(blog.getUploadedRawUrlList());
                if (urlList.isEmpty()) {
                    Snackbar.make(coordinatorLayout, R.string.no_download_image, Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                fabDownload.setColorNormalResId(R.color.primary);
                fabDownload.setTitle(getString(R.string.downloading_image));
                startActivityForResult(PermissionRequireActivity
                        .getDownloadStartIntent(getActivity(), urlList), DOWNLOAD_LIST);
            }
        });

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator);

        webView = (WebView) view.findViewById(R.id.browser);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDownloadConfirmDialog((WebView) view);
                return false;
            }
        });
        webView.setWebViewClient(new BrowserViewClient());

        webView.loadUrl(blog.getUrl());

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
                    startActivityForResult(PermissionRequireActivity
                            .getDownloadStartIntent(getActivity(), url), DOWNLOAD);
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
            NotYetRead.delete(blog.getEntryObjectId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            fabDownload.setColorNormalResId(R.color.accent);
            fabDownload.setTitle(getString(R.string.download_image));
            Snackbar.make(coordinatorLayout, R.string.no_permission_download, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        switch (requestCode) {
            case DOWNLOAD:
                if (!new SimpleImageDownloader(getActivity(), data.getStringArrayListExtra(
                        PermissionRequireActivity.ExtraKey.DOWNLOAD)).get()) {
                    Snackbar.make(coordinatorLayout, R.string.failed_to_download, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case DOWNLOAD_LIST:
                if (!new WaitMinimunImageDownloader(getActivity(), data.getStringArrayListExtra(
                        PermissionRequireActivity.ExtraKey.DOWNLOAD)).get()) {
                    Snackbar.make(coordinatorLayout, R.string.failed_to_download, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    /**
     * WaitMinimunImageDownloader のコールバック
     * @param callback callback
     */
    @Subscribe
    public void onFinishDownload(
            WaitMinimunImageDownloader.Callback.CompleteDownloadImage callback) {
        fabDownload.setColorNormalResId(R.color.accent);
        fabDownload.setTitle(getString(R.string.download_image));
        if (callback != null
                && !callback.responseList.isEmpty()
                && callback.responseList != null) {
            for (int i = callback.responseList.size(); 0 < i; --i) {
                WaitMinimunImageDownloader.Response response = callback.responseList.get(i - 1);
                File file = response.getFile();
                if (file != null) {
                    scanComplete(getActivity(), file);
                    return;
                }
            }
        }
    }

    /**
     * SimpleImageDownloader のコールバック
     * @param callback callback
     */
    @Subscribe
    public void onFinishDownload(SimpleImageDownloader.Callback callback) {
        fabDownload.setColorNormalResId(R.color.accent);
        fabDownload.setTitle(getString(R.string.download_image));
        if (callback != null && callback.file != null) {
            scanComplete(getActivity(), callback.file);
        }
    }

    private void scanComplete(@Nullable Context context, @NonNull File file) {
        scanComplete(context, file, true);
    }

    private void scanComplete(@Nullable Context context, @NonNull File file, final boolean showSnackbar) {
        if (context == null) return;
        SdCardUtils.scanFile(context, file,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, final Uri uri) {
                        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (showSnackbar) showSnackbar(uri);
                            }
                        });
                    }
                });
    }

    private void showSnackbar(final Uri uri) {
        Snackbar.make(coordinatorLayout, R.string.download_finish, Snackbar.LENGTH_LONG)
                .setAction("確認する", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "image/jpeg");
                        startActivity(intent);
                    }
                })
                .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent))
                .show();
    }

    public boolean goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

}
