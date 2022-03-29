package com.example.anothernfcapp.screens.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.anothernfcapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //super.onCreate(savedInstanceState);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
