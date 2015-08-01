package com.tixon.fastfb2;

public class Section {
    public String title;
    public String subtitle;
    public String text;

    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String P = "p";

    public Section(String title, String subtitle, String text) {
        this.title = title;
        this.subtitle = subtitle;
        this.text = text;
    }
}
