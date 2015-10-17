package shts.jp.android.nogifeed.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import shts.jp.android.nogifeed.R;

public class SettingsFragment extends PreferenceFragment {

    /** ブログ更新通知可否設定 */
    private static final String NOTIFICATION_ENABLE = "pref_key_blog_updated_notification_enable";
    /** ブログ更新通知制限設定(お気に入りメンバーのみ通知する設定) */
    private static final String NOTIFICATION_RESTRICTION_ENABLE = "pref_key_blog_updated_notification_restriction_enable";

    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updateView();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void updateView() {
        CheckBoxPreference enableNotification
                = (CheckBoxPreference) findPreference(NOTIFICATION_ENABLE);
        CheckBoxPreference restrictionNotification
                = (CheckBoxPreference) findPreference(NOTIFICATION_RESTRICTION_ENABLE);
        restrictionNotification.setEnabled(enableNotification.isChecked());
    }
}
