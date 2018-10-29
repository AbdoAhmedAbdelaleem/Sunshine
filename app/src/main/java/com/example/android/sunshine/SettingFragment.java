package com.example.android.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences screenSharedPreferences;
    PreferenceScreen preferenceScreen;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.seeting_prference);
        preferenceScreen = getPreferenceScreen();
        screenSharedPreferences = preferenceScreen.getSharedPreferences();
        screenSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            setSummary(preferenceScreen.getPreference(i));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        screenSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    public void setSummary(Preference preference)
    {
        String key = preference.getKey();
        String storedData = screenSharedPreferences.getString(key, "");
        if(preference instanceof EditTextPreference)
        {
            preference.setSummary(storedData);
        }
        else if(preference instanceof ListPreference)
        {
            preference.setSummary(storedData);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        setSummary(preference);
    }
}
