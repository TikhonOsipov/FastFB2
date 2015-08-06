package com.tixon.fastfb2;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Body {
    //public String title;
    //public String epigraph;

    public static final String SECTION = "section";

    public static ArrayList<Section> chapters;

    String tempText;

    public Body(NodeList body) {
        chapters = new ArrayList<>();
        getChapters(body);
    }

    //Move down through tree
    /**
     *
     * @param where: child nodes from element
     */
    public void getChapters(NodeList where) {
        for(int i = 0; i < where.getLength(); i++) {
            if(where.item(i) instanceof Element) {
                if((where.item(i) != null) && ((Element) where.item(i)).getTagName().equals(SECTION)) {
                    getSectionContent(where.item(i).getChildNodes(), chapters);
                }
            }
        }
    }

    public void getSectionContent(NodeList section, ArrayList<Section> chapters) {
        StringBuilder text, title, subtitle;
        text = new StringBuilder();
        title = new StringBuilder();
        subtitle = new StringBuilder();
        ArrayList<String> subtitles = new ArrayList<>();
        ArrayList<String> texts = new ArrayList<>();

        for(int i = 0; i < section.getLength(); i++) {
            if(section.item(i) instanceof Element) {
                //Get title
                if((section.item(i) != null) && ((Element) section.item(i)).getTagName().equals(Section.TITLE)) {
                    title = new StringBuilder(section.item(i).getTextContent());
                }
                //Get subtitle
                if((section.item(i) != null) && ((Element) section.item(i)).getTagName().equals(Section.SUBTITLE)) {
                    //If it's a new subtitle -- add subtitle and text to lists
                    if(subtitle.length() != 0) {
                        subtitles.add(subtitle.toString());
                        texts.add(text.toString());
                    }
                    subtitle = new StringBuilder(section.item(i).getTextContent());
                    text = new StringBuilder();
                }
                //Get text from subtitle
                if((section.item(i) != null) && ((Element) section.item(i)).getTagName().equals(Section.P)) {
                    text.append(section.item(i).getTextContent());
                }
            }
            //If it's last row in section -- add subtitle and text to lists
            if(i == section.getLength() - 1) {
                subtitles.add(subtitle.toString());
                texts.add(text.toString());
            }
        }
        chapters.add(new Section(title.toString(), subtitles, texts));
    }
}
