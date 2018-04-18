package shts.jp.android.nogifeed.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_calender.*
import shts.jp.android.nogifeed.R

class CalenderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_calender, null)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    if (it.url?.host == "sp.nogizaka46.com") {
                        return false
                    }
                    startActivity(Intent(Intent.ACTION_VIEW, it.url))
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        webView.loadUrl(URL)
    }

    fun goBack(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    companion object {
        private val URL: String = "http://sp.nogizaka46.com/qschedule?type=monthly&ymd=&cat=&nm="

        fun newInstance(): CalenderFragment {
            return CalenderFragment()
        }
    }
}
