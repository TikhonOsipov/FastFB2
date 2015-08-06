package com.tixon.fastfb2;

import android.app.Activity;
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
    ArrayList<Card> cards;
    ArrayList<Integer> indexes;
    int counter;
    private static final String KEY_CHAPTER = "key_chapter";
    private static final String KEY_SUBTITLE = "key_subtitle";
    private static final String NO_DATA = "no_data";

    public SectionsRecyclerAdapter(Activity activity, ArrayList<String> titles, ArrayList<String> subtitles,
                                   ArrayList<String> texts) {
        this.titles = titles;
        this.subtitles = subtitles;
        this.texts = texts;
        this.activity = activity;
        this.cards = new ArrayList<>();
        this.indexes = new ArrayList<>();
        this.counter = 0;
        setContent();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_section, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_title.setText(cards.get(position).title);
        holder.tv_subtitle.setText(cards.get(position).subtitle);
        holder.tv_text.setText(cards.get(position).text);
        final int[] index = getPositions(position);
        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainActivity = new Intent();
                backToMainActivity.putExtra(KEY_CHAPTER, index[0]);
                backToMainActivity.putExtra(KEY_SUBTITLE, index[1]);
                activity.setResult(Activity.RESULT_OK, backToMainActivity);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
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

    public class Card {
        String title;
        String subtitle;
        String text;

        public Card(String title, String subtitle, String text) {
            this.title = title;
            this.subtitle = subtitle;
            this.text = text;
        }

        public Card(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }

    public void setContent() {
        String[] mSubtitles, mTexts;
        for(int i = 0; i < titles.size(); i++) {
            mSubtitles = subtitles.get(i).split("\n");
            mTexts = texts.get(i).split("\n");
            if(mSubtitles.length == 0) cards.add(new Card(titles.get(i), mTexts[0]));
            else cards.add(new Card(titles.get(i), mSubtitles[0], mTexts[0]));
            indexes.add(counter);
            if(mSubtitles.length > 1)  {
                if(mSubtitles.length == mTexts.length)
                for(int j = 1; j < mSubtitles.length; j++) {
                    cards.add(new Card("", mSubtitles[j], mTexts[j]));
                    counter++;
                }
                if(mTexts.length == 1) {
                    for(int j = 1; j < mSubtitles.length; j++) {
                        cards.get(cards.size()-1).subtitle += "\n" + mSubtitles[j];
                    }
                }
            }
        }
    }

    public int[] getPositions(int globalPosition) {
        int[] positions = new int[2];
        for(int i = 0; i < indexes.size() - 1; i++) {
            if((indexes.get(i) <= globalPosition) && (globalPosition < indexes.get(i+1))) {
                positions[0] = i;
                break;
            }
            if(indexes.get(indexes.size() - 1) <= globalPosition) {
                positions[0] = indexes.size() - 1;
                break;
            }
        }
        positions[1] = globalPosition - indexes.get(positions[0]) - positions[0];
        return positions;
    }
}
