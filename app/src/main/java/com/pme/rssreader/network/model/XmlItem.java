package com.pme.rssreader.network.model;

import com.pme.rssreader.storage.model.Item;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class XmlItem {
    @Element
    public String title;

    @Element
    public String link;

    @Element(required = false)
    public String pubDate;

    @Element(name= "content", required = false)
    public String content;

    @Element(name= "encoded", required = false)
    public String encodedContent;

    @Element(required = false)
    public String description;

    @Element(required = false)
    public String guid;

    @Override
    public String toString() {
        return "RssItem [title=" + title + ", link=" + link + ", pubDate=" + pubDate
                + ", description=" + description + "]";
    }

    public Item toItem() {
        Item i = new Item();
        i.setTitle(title);
        i.setDescription(description);
        i.setLink(link);
        i.setPubDate(pubDate);
        if (guid != null) {
            i.setGuid(guid);
        }
        if (encodedContent != null) {
            i.setContent(encodedContent);
        } else {
            i.setContent(content);
        }
        return i;
    }
}
