package com.honeydo5.honeydo.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.honeydo5.honeydo.R;

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
