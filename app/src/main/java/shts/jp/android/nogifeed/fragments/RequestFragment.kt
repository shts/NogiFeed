package shts.jp.android.nogifeed.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_request.*
import shts.jp.android.nogifeed.R

class RequestFragment : Fragment() {

    companion object {
        val DESCRIPTION = "お急ぎの場合、<a href=\"https://twitter.com/nogifeed\">Twitter @nogifeed </a>へリプライしていただくと返信が早いかもしれません"

        fun newInstance(): RequestFragment = RequestFragment()
    }

    private val appVersion: String
        get() {
            val context = activity
            val appPackageName = context.packageName

            try {
                val pm = context.packageManager
                val p = pm.getPackageInfo(appPackageName, 0)

                val appVersionName = p.versionName
                val appVersionCode = p.versionCode

                val builder = StringBuilder()
                builder.append("アプリ情報").append("\n")
                builder.append("=========================").append("\n")
                builder.append("メッセージ").append("\n")
                builder.append("appPackageName=").append(appPackageName).append("\n")
                builder.append("appVersion=").append(appVersionName)
                        .append(" (").append(appVersionCode).append(")\n")
                return builder.toString()

            } catch (e: PackageManager.NameNotFoundException) {
                throw IllegalStateException("never reached", e)
            }
        }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_request, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movementMethod = LinkMovementMethod.getInstance()
        link.movementMethod = movementMethod
        @Suppress("DEPRECATION")
        link.text = Html.fromHtml(DESCRIPTION)


        sendButton.setOnClickListener {
            startActivity(createSendRequestIntent(requestEditor.text.toString()))
        }

        toolbar.setTitle(R.string.nav_menu_request)
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp)
        toolbar.setNavigationOnClickListener { activity.finish() }
    }

    private fun createSendRequestIntent(message: String): Intent {
        return ShareCompat.IntentBuilder.from(activity)
                .addEmailTo("nogifeed@gmail.com")
                .setSubject("NogiFeed - フィードバック")
                .setText(appVersion + message)
                .setType("text/plain")
                .intent
    }
}
