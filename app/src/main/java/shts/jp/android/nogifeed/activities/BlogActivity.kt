package shts.jp.android.nogifeed.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_blog.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.providers.dao.UnreadArticles
import shts.jp.android.nogifeed.utils.FileDownloader
import shts.jp.android.nogifeed.utils.PreferencesUtils
import shts.jp.android.nogifeed.utils.SdCardUtils
import shts.jp.android.nogifeed.utils.ShareUtils
import java.io.File
import java.util.*

class BlogActivity : AppCompatActivity() {

    companion object {
        private val DOWNLOAD = 0
        private val DOWNLOAD_LIST = 1

        private val EXTRA_KEY = "entry"

        fun getStartIntent(context: Context, entry: Entry): Intent {
            val intent = Intent(context, BlogActivity::class.java)
            intent.putExtra(EXTRA_KEY, entry)
            return intent
        }
    }

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        val entry = intent.getParcelableExtra<Entry>(EXTRA_KEY)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = entry?.title
            it.subtitle = entry?.memberName
        }

        fabShare.setOnClickListener {
            fab.collapse()
            entry.let { startActivity(ShareUtils.getShareBlogIntent(it)) }
        }

        fabDownload.setOnClickListener({
            downloadImages(entry)
        })

        webView.settings.javaScriptEnabled = true
        webView.setOnLongClickListener {
            showDownloadConfirmDialog(it as WebView?)
            false
        }
        webView.webViewClient = BrowserViewClient()

        entry?.let {
            webView.loadUrl(it.url)
            UnreadArticles.remove(this, it.url)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun downloadImages(entry: Entry) {
        val urlList = ArrayList<String>()
        if (downloadThumbnail()) {
            urlList.addAll(entry.uploadedThumbnailUrls)
        }

        // 高画質画像が有効期限切れのトーストを表示する
        if (entry.uploadedRawImageUrls == null || entry.uploadedRawImageUrls.isEmpty()) {
            Toast.makeText(this, R.string.recomend_download_thumbnail, Toast.LENGTH_SHORT).show()
        } else {
            urlList.addAll(entry.uploadedRawImageUrls)
        }

        if (urlList.isEmpty()) {
            if (downloadThumbnail()) {
                Snackbar.make(coordinator, R.string.no_download_image, Snackbar.LENGTH_LONG)
                        .show()
            } else {
                Snackbar.make(coordinator, R.string.recomend_download_thumbnail, Snackbar.LENGTH_LONG)
                        .show()
            }
            return
        }
        startActivityForResult(PermissionRequireActivity
                .getDownloadStartIntent(this, urlList), DOWNLOAD_LIST)
    }

    private inner class BrowserViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (request?.url?.host?.equals("blog.nogizaka46.com") == true) {
                return super.shouldOverrideUrlLoading(view, request)
            }
            startActivity(Intent(Intent.ACTION_VIEW, request?.url))
            return true
        }
    }

    private fun downloadThumbnail(): Boolean {
        return PreferencesUtils.getBoolean(this, getString(R.string.setting_enable_thumbnail_download_key), true)
    }

    private fun showDownloadConfirmDialog(webView: WebView?) {
        val hr = webView?.hitTestResult
        if (WebView.HitTestResult.IMAGE_TYPE == hr?.type
                || WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE == hr?.type) {
            val url = hr.extra
            AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_confirm_download_title)
                    .setMessage(R.string.dialog_confirm_download_message)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        url.let { startActivityForResult(PermissionRequireActivity
                                .getDownloadStartIntent(BlogActivity@this, it), DOWNLOAD) }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            fabDownload.setColorNormalResId(R.color.accent)
            fabDownload.title = getString(R.string.download_image)
            Snackbar.make(coordinator, R.string.no_permission_download, Snackbar.LENGTH_LONG)
                    .show()
            return
        }

        when (requestCode) {
            DOWNLOAD -> {
                data?.getStringArrayListExtra(PermissionRequireActivity.ExtraKey.DOWNLOAD)?.get(0)?.let {
                    onDownloadingFab()
                    val f = File(SdCardUtils.getDownloadFilePath(it))
                    subscriptions.add(FileDownloader.exec(this, it, f)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ uri ->
                                onDownloadedFab()
                                showSnackbar(uri)
                            }, {
                                Snackbar.make(coordinator, R.string.failed_to_download, Snackbar.LENGTH_LONG).show()
                            }))
                } ?: run {
                    onDownloadedFab()
                }
            }
            DOWNLOAD_LIST -> {
                data?.getStringArrayListExtra(PermissionRequireActivity.ExtraKey.DOWNLOAD)?.let {
                    onDownloadingFab()
                    val list: MutableList<File> = it
                            .map { File(SdCardUtils.getDownloadFilePath(it)) }
                            .toMutableList()
                    subscriptions.add(FileDownloader.exec(this, it, list)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ uri ->
                                onDownloadedFab()
                                showSnackbar(uri)
                            }, {
                                Snackbar.make(coordinator, R.string.failed_to_download, Snackbar.LENGTH_LONG).show()
                            }))
                } ?: run {
                    onDownloadedFab()
                }
            }
        }
    }

    private fun onDownloadingFab() {
        fabDownload.setColorNormalResId(R.color.primary)
        fabDownload.title = getString(R.string.downloading_image)
    }

    private fun onDownloadedFab() {
        fabDownload.setColorNormalResId(R.color.accent)
        fabDownload.title = getString(R.string.download_image)
    }

    private fun showSnackbar(uri: Uri) {
        Snackbar.make(coordinator, R.string.download_finish, Snackbar.LENGTH_LONG)
                .setAction(R.string.confirm) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(uri, "image/jpeg")
                    startActivity(intent)
                }
                .setActionTextColor(ContextCompat.getColor(this, R.color.accent))
                .show()
    }

    override fun onBackPressed() {
        if (fab.isExpanded) {
            fab.collapse()
            return
        }

        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        subscriptions.unsubscribe()
        super.onDestroy()
    }
}
