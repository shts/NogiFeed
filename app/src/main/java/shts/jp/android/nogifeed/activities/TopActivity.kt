package shts.jp.android.nogifeed.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_top.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.fragments.*
import shts.jp.android.nogifeed.utils.PreferencesUtils

class TopActivity : AppCompatActivity() {

    companion object {
        private val TAG = TopActivity::class.java.simpleName
        private val KEY_PREF = "pre-fragment"
    }

    private var lastSelectedMenuId: Int
        get() = PreferencesUtils.getInt(this, KEY_PREF, R.id.menu_all_feed)
        set(id) = PreferencesUtils.setInt(this, KEY_PREF, id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        toolbar.setNavigationOnClickListener { drawer.openDrawer(navigation) }

        navigation.setNavigationItemSelectedListener(
                NavigationView.OnNavigationItemSelectedListener { menuItem ->
                    drawer.closeDrawers()
                    val id = menuItem.itemId
                    if (id == lastSelectedMenuId) {
                        return@OnNavigationItemSelectedListener false
                    }
                    setupFragment(id)
                    false
                })
        setupFragment(lastSelectedMenuId)
    }

    private fun setupFragment(id: Int) {
        val fragment: Fragment
        when (id) {
            R.id.menu_all_feed -> fragment = AllFeedListFragment.newInstance
            R.id.menu_fav_member_feed -> fragment = FavoriteMemberFeedListFragment.newInstance
            R.id.menu_member -> fragment = MemberGridFragment.newInstance()
            R.id.menu_news -> fragment = NewsListFragment.newInstance
            R.id.menu_calender -> fragment = CalenderFragment.newInstance()
            R.id.menu_settings -> fragment = SettingsFragment()
            R.id.menu_about_app, R.id.menu_request, R.id.menu_lisences -> {
                startActivity(OtherMenuActivity.getStartIntent(this, id))
                return
            }
            else -> {
                Log.e(TAG, "failed to change fragment")
                return
            }
        }

        navigation.menu.findItem(id).isChecked = true
        toolbar.title = navigation.menu.findItem(id).title

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, fragment.javaClass.simpleName)
        ft.commit()
        lastSelectedMenuId = id
    }

    override fun onBackPressed() {
        // カレンダーはWebViewなのでバックキーの消化判定を行なう
        val calenderFragment = supportFragmentManager.findFragmentByTag(
                CalenderFragment::class.java.simpleName) as CalenderFragment?

        calenderFragment?.let {
            if (it.isVisible && it.goBack()) {
                return
            }
        }

        super.onBackPressed()
    }
}
