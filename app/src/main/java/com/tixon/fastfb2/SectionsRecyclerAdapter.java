package com.tixon.fastfb2;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SectionsRecyclerAdapter extends RecyclerView.Adapter<SectionsRecyclerAdapter.ViewHolder> {
    //Context context;
    ArrayList<String> titles, subtitles;

    public SectionsRecyclerAdapter(ArrayList<String> titles, ArrayList<String> subtitles) {
        this.titles = titles;
        this.subtitles = subtitles;
        //this.context = context;
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
        holder.tv_subtitle.setText(subtitles.get(position));
        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tv_title, tv_subtitle;
        FrameLayout frame;
        public ViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
            tv_title = (TextView) cv.findViewById(R.id.tv_section_title);
            tv_subtitle = (TextView) cv.findViewById(R.id.tv_section_subtitle);
            frame = (FrameLayout) cv.findViewById(R.id.section_frame); //click handling view
        }
    }
}
