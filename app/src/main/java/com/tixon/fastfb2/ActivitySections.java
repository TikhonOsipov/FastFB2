package com.tixon.fastfb2;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

public class ActivitySections extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SectionsRecyclerAdapter adapter;
    ArrayList<String> titles, subtitles, texts;
    SharedPreferences preferences;

    int color;

    private static final String KEY_TITLES = "array_list_titles";
    private static final String KEY_SUBTITLES = "array_list_subtitles";
    private static final String KEY_TEXTS = "array_list_texts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        color = Integer.parseInt(preferences.getString("app_color", "8"));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.activity_sections_subtitle);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        titles = getIntent().getStringArrayListExtra(KEY_TITLES);
        subtitles = getIntent().getStringArrayListExtra(KEY_SUBTITLES);
        texts = getIntent().getStringArrayListExtra(KEY_TEXTS);

        adapter = new SectionsRecyclerAdapter(this, titles, subtitles, texts);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.sections_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
