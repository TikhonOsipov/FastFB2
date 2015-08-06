package com.tixon.fastfb2;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

public class ActivitySettings extends AppCompatActivity {
    public static Toolbar toolbar;
    public static Window window;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        window = getWindow();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.activity_settings_subtitle);
        setSupportActionBar(toolbar);
        try { getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        catch (NullPointerException e) { e.printStackTrace(); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int color = Integer.parseInt(preferences.getString("app_color", "8"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getIntArray(R.array.colors700)[color]);
        toolbar.setBackgroundColor(getResources().getIntArray(R.array.colors500)[color]);
        setTextColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setTextColor(int color) {
        switch(color) {
            case 0:case 1:case 2:case 3:case 4:case 5:case 6:case 8:case 14:case 15:case 17:
                //white
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
                //super.setTheme(R.style.AppThemeDark);
                break;
            default:
                //black
                toolbar.setTitleTextColor(getResources().getColor(R.color.black));
                toolbar.setSubtitleTextColor(getResources().getColor(R.color.black));
                //super.setTheme(R.style.AppThemeLight);
                break;
        }
    }
}
