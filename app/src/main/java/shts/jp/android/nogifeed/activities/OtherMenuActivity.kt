package shts.jp.android.nogifeed.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.fragments.AboutFragment
import shts.jp.android.nogifeed.fragments.LicenseFragment
import shts.jp.android.nogifeed.fragments.RequestFragment

/**
 * ドロワーのそのほかメニューから遷移する画面
 */
class OtherMenuActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_MENU_ID = "menu_id"

        fun getStartIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, OtherMenuActivity::class.java)
            intent.putExtra(EXTRA_MENU_ID, id)
            return intent
        }
    }

    private fun getFragmentFrom(id: Int): Fragment? {
        when (id) {
            R.id.menu_lisences -> return LicenseFragment()
            R.id.menu_request -> return RequestFragment()
            R.id.menu_about_app -> return AboutFragment()
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        val fragment = getFragmentFrom(intent.getIntExtra(EXTRA_MENU_ID, -1))
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, OtherMenuActivity::class.java.simpleName)
        ft.commit()
    }
}
