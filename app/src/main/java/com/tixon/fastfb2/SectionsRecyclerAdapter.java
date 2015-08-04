package com.tixon.fastfb2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SectionsRecyclerAdapter extends RecyclerView.Adapter<SectionsRecyclerAdapter.ViewHolder> {
    Activity activity;
    ArrayList<String> titles, subtitles, texts;
    private static final String KEY_CHAPTER = "key_chapter";

    public SectionsRecyclerAdapter(Activity activity, ArrayList<String> titles, ArrayList<String> subtitles,
                                   ArrayList<String> texts) {
        this.titles = titles;
        this.subtitles = subtitles;
        this.texts = texts;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_section, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_title.setText(titles.get(position));
        if(subtitles.get(position).equals("")) holder.tv_subtitle.setVisibility(View.GONE);
        else holder.tv_subtitle.setText(subtitles.get(position));
        holder.tv_text.setText(texts.get(position));
        final int chapterIndex = position;
        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainActivity = new Intent();
                backToMainActivity.putExtra(KEY_CHAPTER, chapterIndex);
                activity.setResult(Activity.RESULT_OK, backToMainActivity);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tv_title, tv_subtitle, tv_text;
        FrameLayout frame;
        public ViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
            tv_title = (TextView) cv.findViewById(R.id.tv_section_title);
            tv_subtitle = (TextView) cv.findViewById(R.id.tv_section_subtitle);
            tv_text = (TextView) cv.findViewById(R.id.tv_section_text);
            frame = (FrameLayout) cv.findViewById(R.id.section_frame); //click handling view
        }
    }
}
