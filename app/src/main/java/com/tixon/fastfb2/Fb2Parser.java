package com.tixon.fastfb2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Fb2Parser {
    public ArrayList<Element> elements;

    public static ArrayList<Section> sections;
    public static TitleInfo titleInfo;
    public static DocumentInfo documentInfo;
    public static Body bodyMain;

    //root
    public static final String DESCRIPTION = "description";
    public static final String BODY = "body";
    //body
    //description
    public static final String TITLE_INFO = "title-info";
    public static final String SRC_TITLE_INFO = "src-title-info";
    public static final String DOCUMENT_INFO = "document-info";

    Element documentElement;

    public Fb2Parser(Document document) {
        elements = new ArrayList<>();
        sections = new ArrayList<>();
        documentElement = document.getDocumentElement();
    }

    //?????????? ????? ?????
    public void parse() {
        //StringBuilder text = new StringBuilder();
        NodeList root = documentElement.getChildNodes();
        ArrayList<NodeList> tagsBody = move(root, BODY);
        NodeList tagsDescription = getSingleInfo(root, DESCRIPTION);
        bodyMain = new Body(tagsBody.get(0));
        titleInfo = new TitleInfo(getSingleInfo(tagsDescription, TITLE_INFO));
        documentInfo = new DocumentInfo(getSingleInfo(tagsDescription, DOCUMENT_INFO));
    }

    //Move down through tree
    /**
     *
     * @param where: child nodes from element
     * @param condition: filter
     * @return ArrayList<NodeList> with child nodes of element with tag @param condition
     */
    public ArrayList<NodeList> move(NodeList where, String condition) {
        ArrayList<NodeList> list = new ArrayList<>();
        NodeList tempNodeList;
        for(int i = 0; i < where.getLength(); i++) {
            if(where.item(i) instanceof Element) {
                if((where.item(i) != null) && ((Element) where.item(i)).getTagName().equals(condition)) {
                    tempNodeList = where.item(i).getChildNodes();
                    list.add(tempNodeList);
                }
            }
        }
        return list;
    }

    public NodeList getSingleInfo(NodeList where, String condition) {
        NodeList nodeList = null;
        for(int i = 0; i < where.getLength(); i++) {
            if(where.item(i) instanceof Element) {
                if((where.item(i) != null) && ((Element) where.item(i)).getTagName().equals(condition)) {
                    nodeList = where.item(i).getChildNodes();
                }
            }
        }
        return nodeList;
    }
}
