package shts.jp.android.nogifeed.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentUtils {

    private val URL_TWITTER = "https://twitter.com/"
    private val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=shts.jp.android.nogifeed"
    private val RECOMEND_TEXT = "乃木坂46公式ブログは NogiFeed で読みましょう！"

    fun showDeveloper(context: Context) {
        val uri = Uri.parse(URL_TWITTER + "shts_dev")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun inquiryApp(context: Context) {
        val uri = Uri.parse(URL_TWITTER + "nogifeed")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun rateApp(context: Context) {
        val uri = Uri.parse(PLAY_STORE_URL)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun recommendApp(context: Context) {
        val text = RECOMEND_TEXT + "\n" + PLAY_STORE_URL
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(intent)
    }
}
