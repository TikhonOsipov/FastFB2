package com.tixon.fastfb2;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DocumentInfo {
    public String homePage, url, date, eMail, srcOcr, id, version, history;
    public String authorNickname, authorFirstName, authorMiddleName, authorLatName;

    public static final String ID = "id";
    public static final String VERSION = "version";
    public static final String HISTORY = "history";

    public DocumentInfo(String id, String version, String history) {
        this.id = id;
        this.version = version;
        this.history = history;
    }

    public DocumentInfo(NodeList documentInfo) {
        this.id = getSingleInfo(documentInfo, ID);
        this.version = getSingleInfo(documentInfo, VERSION);
        //this.history = getText(documentInfo, HISTORY);
    }

    public String getSingleInfo(NodeList where, String condition) {
        String info = "";
        for(int i = 0; i < where.getLength(); i++) {
            if(where.item(i) instanceof Element) {
                if(((Element) where.item(i) != null) && ((Element) where.item(i)).getTagName().equals(condition)) {
                    info =  where.item(i).getTextContent();
                }
            }
        }
        return info;
    }

    public String getText(NodeList where, String condition) {
        StringBuilder text = new StringBuilder();
        NodeList childNodes = null;
        NodeList tempNodeList = where;
        for(int i = 0; i < tempNodeList.getLength(); i++) {
            if(tempNodeList.item(i) instanceof Element) {
                if(((Element) tempNodeList.item(i) != null) && ((Element) tempNodeList.item(i)).getTagName().equals(condition)) {
                    childNodes = tempNodeList.item(i).getChildNodes();
                    while(childNodes != null) {
                        for(int j = 0; j < childNodes.getLength(); j++) {
                            text.append(childNodes.item(j).getTextContent());
                            childNodes = childNodes.item(j).getChildNodes();
                        }
                    }
                }
            }
        }
        return text.toString();
    }
}
