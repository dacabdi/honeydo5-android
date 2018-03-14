package com.honeydo5.honeydo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by aaron on 2/27/2018.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
    }
}
