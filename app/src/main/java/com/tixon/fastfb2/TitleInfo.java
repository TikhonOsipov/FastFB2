package com.tixon.fastfb2;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TitleInfo extends Info {
    public static String genre, bookTitle, annotation, date, lang, srcLang;

    public static Person author, translator;

    public static final String GENRE = "genre";
    public static final String AUTHOR = "author";
    public static final String BOOK_TITLE = "book-title";
    public static final String ANNOTATION = "annotation";
    public static final String DATE = "date";
    public static final String LANG = "lang";
    public static final String SRC_LANG = "src-lang";
    public static final String TRANSLATOR = "translator";
    public static final String FIRST_NAME = "first-name";
    public static final String MIDDLE_NAME = "middle-name";
    public static final String LAST_NAME = "last-name";

    /*public TitleInfo(String genre, String bookTitle, String annotation, String date, String lang, String srcLang) {
        genre = genre;
        bookTitle = bookTitle;
        annotation = annotation;
        date = date;
        lang = lang;
        srcLang = srcLang;
    }*/

    public TitleInfo(NodeList titleInfo) {
        genre = getSingleInfo(titleInfo, GENRE);
        bookTitle = getSingleInfo(titleInfo, BOOK_TITLE);
        annotation = getSingleInfo(titleInfo, ANNOTATION);
        date = getSingleInfo(titleInfo, DATE);
        lang = getSingleInfo(titleInfo, LANG);
        srcLang = getSingleInfo(titleInfo, SRC_LANG);
        author = getPerson(titleInfo, AUTHOR);
        translator = getPerson(titleInfo, TRANSLATOR);
    }

    public String getSingleInfo(NodeList where, String condition) {
        String info = "";
        for(int i = 0; i < where.getLength(); i++) {
            if(where.item(i) instanceof Element) {
                if((where.item(i) != null) && ((Element) where.item(i)).getTagName().equals(condition)) {
                    info =  where.item(i).getTextContent();
                }
            }
        }
        return info;
    }

    public Person getPerson(NodeList parent, String conditionParent) {
        NodeList personNodeList = null;
        String firstName = "", middleName = "", lastName = "";
        for(int i = 0; i < parent.getLength(); i++) {
            if(parent.item(i) instanceof Element) {
                if((parent.item(i) != null) && ((Element) parent.item(i)).getTagName().equals(conditionParent)) {
                    personNodeList = parent.item(i).getChildNodes();
                    break;
                }
            }
        }
        if(personNodeList != null) {
            for(int i = 0; i < personNodeList.getLength(); i++) {
                if(personNodeList.item(i) instanceof Element) {
                    if((personNodeList.item(i) != null)) {
                        if(((Element) personNodeList.item(i)).getTagName().equals(FIRST_NAME)) {
                            firstName = personNodeList.item(i).getTextContent();
                        }
                        else if(((Element) personNodeList.item(i)).getTagName().equals(MIDDLE_NAME)) {
                            middleName = personNodeList.item(i).getTextContent();
                        }
                        else if(((Element) personNodeList.item(i)).getTagName().equals(LAST_NAME)) {
                            lastName = personNodeList.item(i).getTextContent();
                        }
                    }
                }
            }
        }
        return new Person(firstName, middleName, lastName);
    }
}
