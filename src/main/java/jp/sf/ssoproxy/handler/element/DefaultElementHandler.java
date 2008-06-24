package jp.sf.ssoproxy.handler.element;

import jp.sf.ssoproxy.handler.html.HtmlHandler;

import org.xml.sax.Attributes;

public class DefaultElementHandler implements ElementHandler {

    public void startElement(HtmlHandler htmlHandler, String uri,
            String localName, String name, Attributes attributes) {
        if (htmlHandler.isWritable()) {
            htmlHandler.write(OPEN_TAG_PREFIX);
            htmlHandler.write(name.toLowerCase());
            for (int i = 0; i < attributes.getLength(); i++) {
                htmlHandler.write(SPACE);
                htmlHandler.write(attributes.getQName(i).toLowerCase());
                htmlHandler.write(ATTR_VALUE_BEGIN);
                htmlHandler.write(attributes.getValue(i));
                htmlHandler.write(ATTR_VALUE_END);
            }
            htmlHandler.write(OPEN_TAG_SUFFIX);
        }
    }

    public void endElement(HtmlHandler htmlHandler, String uri,
            String localName, String name) {
        if (htmlHandler.isWritable()) {
            htmlHandler.write(CLOSE_TAG_PREFIX);
            htmlHandler.write(name.toLowerCase());
            htmlHandler.write(CLOSE_TAG_SUFFIX);
        }
    }
}
