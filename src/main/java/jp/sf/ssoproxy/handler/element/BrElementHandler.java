package jp.sf.ssoproxy.handler.element;

import jp.sf.ssoproxy.handler.html.HtmlHandler;

import org.xml.sax.Attributes;

public class BrElementHandler extends DefaultElementHandler {
    private static final String SLASH = "/";

    private boolean endWithSlash;

    public BrElementHandler() {
        this(true);
    }

    public BrElementHandler(boolean endWithSlash) {
        this.endWithSlash = endWithSlash;
    }

    public void startElement(HtmlHandler htmlHandler, String uri,
            String localName, String name, Attributes attributes) {
        if (htmlHandler.isWritable()) {
            htmlHandler.write(OPEN_TAG_PREFIX);
            htmlHandler.write(name);
            for (int i = 0; i < attributes.getLength(); i++) {
                htmlHandler.write(SPACE);
                htmlHandler.write(attributes.getQName(i).toLowerCase());
                htmlHandler.write(ATTR_VALUE_EQUAL);
                htmlHandler.write(getQuotationMark());
                htmlHandler.write(attributes.getValue(i));
                htmlHandler.write(getQuotationMark());
            }
            if (endWithSlash) {
                htmlHandler.write(SLASH);
            }
            htmlHandler.write(OPEN_TAG_SUFFIX);
        }
    }

    public void endElement(HtmlHandler htmlHandler, String uri,
            String localName, String name) {
        // IE deals with </br> as <br>
    }
}
