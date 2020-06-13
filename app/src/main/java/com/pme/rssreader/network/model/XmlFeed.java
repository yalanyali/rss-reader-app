package com.pme.rssreader.network.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class XmlFeed {
    @Element
    public XmlChannel channel;

    @Override
    public String toString() {
        return "RssFeed [channel=" + channel + "]";
    }
}
