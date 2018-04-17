package com.honeydo5.honeydo.app;

import com.honeydo5.honeydo.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by aaron on 2/27/2018.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_mute")) {
            // Set summary to be the user-description for the selected value
            if(sharedPreferences.getBoolean("pref_mute", false)) {
                // TODO: David put the mute thing here
            }
        }
    }

    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preferences);
        }
    }
}
