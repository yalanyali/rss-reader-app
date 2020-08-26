package org.pme.rssreader.network.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * XML feed channel model. A channel has a title, description and a list of items.
 */
@Root(strict = false)
public class XmlChannel
{
    @Element
    public String title;

    @Element(required = false)
    public String description;

    @ElementList(inline = true, required = false)
    public List<XmlItem> item;

    @Override
    public String toString() {
        return "Channel [item=" + item + "]";
    }
}