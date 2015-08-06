package com.tixon.fastfb2;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.Window;

public class FragmentPreferences extends PreferenceFragment {

    public static FragmentPreferences newInstance() {
        return new FragmentPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        ListPreference listColor = (ListPreference) findPreference("app_color");
        listColor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int color = Integer.parseInt((String) newValue);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    ActivitySettings.window.setStatusBarColor(getResources().getIntArray(R.array.colors700)[color]);
                ActivitySettings.toolbar.setBackgroundColor(getResources()
                        .getIntArray(R.array.colors500)[color]);
                setTextColor(color);
                return true;
            }
        });

        EditTextPreference wps = (EditTextPreference) findPreference("words_per_minute");
        wps.setSummary(getPreferenceManager().getSharedPreferences().getString("words_per_minute", "300"));
        wps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });
    }

    void setTextColor(int color) {
        switch (color) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            case 14:
            case 15:
            case 17:
                //white
                ActivitySettings.toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                ActivitySettings.toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
                //super.setTheme(R.style.AppThemeDark);
                break;
            default:
                //black
                ActivitySettings.toolbar.setTitleTextColor(getResources().getColor(R.color.black));
                ActivitySettings.toolbar.setSubtitleTextColor(getResources().getColor(R.color.black));
                //super.setTheme(R.style.AppThemeLight);
                break;
        }
    }
}
