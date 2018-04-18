package shts.jp.android.nogifeed.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_news_browse.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.entities.News
import shts.jp.android.nogifeed.utils.ShareUtils
import android.view.WindowManager



class NewsBrowseActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_NEWS = "extra_news"

        fun getStartIntent(context: Context, news: News): Intent =
                Intent(context, NewsBrowseActivity::class.java).apply {
                    putExtra(EXTRA_NEWS, news)
                }
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            var safeUrl = url ?: return
            if (!safeUrl.startsWith("http")) {
                if (safeUrl.startsWith(".")) {
                    safeUrl = safeUrl.replace(".", "http://www.nogizaka46.com/")
                }
            }
            super.onPageStarted(view, safeUrl, favicon)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_browse)

        val news = intent.getParcelableExtra<News>(EXTRA_NEWS)

        webView.webViewClient = webViewClient
        webView.settings.javaScriptEnabled = true

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_18dp)
        supportActionBar?.title = news.title
        supportActionBar?.subtitle = news.date

        fab.setOnClickListener({ startActivity(ShareUtils.getNewsFeedIntent(news)) })

        webView.loadUrl(news.url)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
