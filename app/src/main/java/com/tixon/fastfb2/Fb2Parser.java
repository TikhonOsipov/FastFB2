package com.tixon.fastfb2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Fb2Parser {
    public NodeList nodeList;
    public ArrayList<Element> elements;

    public static ArrayList<Section> sections;
    public static TitleInfo titleInfo;
    public static DocumentInfo documentInfo;

    //root
    public static final String DESCRIPTION = "description";
    public static final String BODY = "body";
    //body
    public static final String SECTION = "section";
    //description
    public static final String TITLE_INFO = "title-info";
    public static final String SRC_TITLE_INFO = "src-title-info";
    public static final String DOCUMENT_INFO = "document-info";
    //title_info

    //document_info

    //author
    public static final String FIRST_NAME = "first-name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    public static final String NICKNAME = "nickname";

    //other tags
    public static final String P = "p";

    Element documentElement;

    public Fb2Parser(Document document) {
        elements = new ArrayList<>();
        sections = new ArrayList<>();
        documentElement = document.getDocumentElement();
    }

    //?????????? ????? ?????
    public String parse() {
        StringBuilder text = new StringBuilder();
        NodeList root = documentElement.getChildNodes();
        ArrayList<NodeList> tagsBody = move(root, BODY);
        NodeList tagsDescription = getSingleInfo(root, DESCRIPTION);
        titleInfo = new TitleInfo(getSingleInfo(tagsDescription, TITLE_INFO));
        documentInfo = new DocumentInfo(getSingleInfo(tagsDescription, DOCUMENT_INFO));

        ArrayList<NodeList> tagsSection = move(tagsBody.get(0), SECTION);
        for (int j = 0; j < tagsSection.size(); j++) {
            text = new StringBuilder();
            text.append(getText(move(tagsSection.get(j), P)));
            sections.add(new Section(getText(move(tagsSection.get(j), Section.TITLE)),
                    getText(move(tagsSection.get(j), Section.SUBTITLE)), text.toString()));
        }

        /*for (int i = 0; i < tagsBody.size(); i++) {
            ArrayList<NodeList> tagsSection = move(tagsBody.get(0), SECTION);
            for (int j = 0; j < tagsSection.size(); j++) {
                text.append(getText(move(tagsSection.get(j), P)));
            }
        }*/
        System.out.println("1");
        return text.toString();
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
        NodeList tempNodeList = null;
        for(int i = 0; i < where.getLength(); i++) {
            if(where.item(i) instanceof Element) {
                if(((Element) where.item(i) != null) && ((Element) where.item(i)).getTagName().equals(condition)) {
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

    //Get text from section
    public String getText(ArrayList<NodeList> list) {
        String text = "";
        for(int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getLength(); j++) {
                Node tempNode = list.get(i).item(j);
                text += tempNode.getTextContent();
            }
        }
        return text;
    }


}
