package jp.sf.ssoproxy.handler.element;

import jp.sf.ssoproxy.handler.html.HtmlHandler;

import org.xml.sax.Attributes;

public interface ElementHandler {
    public static final String OPEN_TAG_PREFIX = "<";

    public static final String OPEN_TAG_SUFFIX = ">";

    public static final String CLOSE_TAG_PREFIX = "</";

    public static final String CLOSE_TAG_SUFFIX = ">";

    public static final String SPACE = " ";

    public static final String ATTR_VALUE_BEGIN = "=\"";

    public static final String ATTR_VALUE_END = "\"";

    public static final String HREF_ATTR = "href";

    public abstract void startElement(HtmlHandler htmlHandler, String uri,
            String localName, String name, Attributes attributes);

    public abstract void endElement(HtmlHandler htmlHandler, String uri,
            String localName, String name);

}