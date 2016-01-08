package shts.jp.android.nogifeed.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import shts.jp.android.nogifeed.R;

import static shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification.RES_ID_NOTIFICATION_ENABLE;
import static shts.jp.android.nogifeed.views.notifications.BlogUpdateNotification.RES_ID_NOTIFICATION_RESTRICTION_ENABLE;

public class SettingsFragment extends PreferenceFragment {

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
        final Resources res = getResources();

        final String keyEnable = res.getString(RES_ID_NOTIFICATION_ENABLE);
        CheckBoxPreference enableNotification
                = (CheckBoxPreference) findPreference(keyEnable);

        final String keyRestrict = res.getString(RES_ID_NOTIFICATION_RESTRICTION_ENABLE);
        CheckBoxPreference restrictionNotification
                = (CheckBoxPreference) findPreference(keyRestrict);
        restrictionNotification.setEnabled(enableNotification.isChecked());
    }
}
