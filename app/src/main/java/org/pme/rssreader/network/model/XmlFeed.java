package org.pme.rssreader.network.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 * XML feed model. A feed has a channel.
 */
@NamespaceList({
    @Namespace(reference = "http://purl.org/rss/1.0/"),
    @Namespace(prefix = "content", reference = "http://purl.org/rss/1.0/modules/content/")
})
@Root(name = "rss", strict = false)
public class XmlFeed {

    @Element(name = "channel")
    public XmlChannel channel;

    @Override
    public String toString() {
        return "RssFeed [channel=" + channel + "]";
    }
}
