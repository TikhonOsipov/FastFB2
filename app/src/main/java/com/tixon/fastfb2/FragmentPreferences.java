package com.tixon.fastfb2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class FragmentPreferences extends PreferenceFragment {

    public static FragmentPreferences newInstance() {
        return new FragmentPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
