package com.tixon.fastfb2;

import java.util.ArrayList;

public class Section {
    public String title;
    public ArrayList<String> subtitles;
    public ArrayList<String> texts;

    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String P = "p";

    public Section(String title, ArrayList<String> subtitles, ArrayList<String> texts) {
        this.title = title;
        this.subtitles = subtitles;
        this.texts = texts;
    }
}
