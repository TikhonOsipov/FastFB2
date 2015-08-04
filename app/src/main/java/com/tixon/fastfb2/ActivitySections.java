package com.tixon.fastfb2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ActivitySections extends ActionBarActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SectionsRecyclerAdapter adapter;
    ArrayList<String> titles, subtitles, texts;

    private static final String KEY_TITLES = "array_list_titles";
    private static final String KEY_SUBTITLES = "array_list_subtitles";
    private static final String KEY_TEXTS = "array_list_texts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);

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
}
