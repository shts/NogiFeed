package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import shts.jp.android.nogifeed.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }
}
