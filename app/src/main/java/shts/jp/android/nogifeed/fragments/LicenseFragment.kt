package shts.jp.android.nogifeed.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_license.*

import shts.jp.android.nogifeed.R

class LicenseFragment : Fragment() {

    companion object {
        fun newInstance(): LicenseFragment = LicenseFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_license, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setTitle(R.string.nav_menu_licenses)
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp)
        toolbar.setNavigationOnClickListener { activity.finish() }
        webView.loadUrl("file:///android_asset/licenses.html")
    }
}
