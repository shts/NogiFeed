package shts.jp.android.nogifeed.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import com.github.machinarius.preferencefragment.PreferenceFragment
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification2

class SettingsFragment : PreferenceFragment() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> updateView() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onResume() {
        super.onResume()
        updateView()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun updateView() {
        val res = resources

        val keyEnable = res.getString(BlogUpdateNotification2.NOTIFICATION_ENABLE)
        val enableNotification = findPreference(keyEnable) as CheckBoxPreference

        val keyRestrict = res.getString(BlogUpdateNotification2.NOTIFICATION_RESTRICTION_ENABLE)
        val restrictionNotification = findPreference(keyRestrict) as CheckBoxPreference
        restrictionNotification.isEnabled = enableNotification.isChecked
    }
}
