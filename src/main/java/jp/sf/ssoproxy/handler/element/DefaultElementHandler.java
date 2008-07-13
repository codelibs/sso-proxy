package jp.sf.ssoproxy.handler.element;

import jp.sf.ssoproxy.handler.html.HtmlHandler;

import org.xml.sax.Attributes;

public class DefaultElementHandler implements ElementHandler {
    protected static final String SLASH = "/";

    private String quotationMark;

    private boolean skipEndTag;

    private boolean endWithSlash;

    public DefaultElementHandler() {
        quotationMark = DEFAULT_QUOTATION_MARK;
        skipEndTag = false;
        endWithSlash = false;
    }

    public void startElement(HtmlHandler htmlHandler, String uri,
            String localName, String name, Attributes attributes) {
        if (htmlHandler.isWritable()) {
            htmlHandler.write(OPEN_TAG_PREFIX);
            htmlHandler.write(name.toLowerCase());
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
        if (!skipEndTag && htmlHandler.isWritable()) {
            htmlHandler.write(CLOSE_TAG_PREFIX);
            htmlHandler.write(name.toLowerCase());
            htmlHandler.write(CLOSE_TAG_SUFFIX);
        }
    }

    public String getQuotationMark() {
        return quotationMark;
    }

    public void setQuotationMark(String quotationMark) {
        this.quotationMark = quotationMark;
    }

    public boolean isSkipEndTag() {
        return skipEndTag;
    }

    public void setSkipEndTag(boolean skipEndTag) {
        this.skipEndTag = skipEndTag;
    }

    public boolean isEndWithSlash() {
        return endWithSlash;
    }

    public void setEndWithSlash(boolean endWithSlash) {
        this.endWithSlash = endWithSlash;
    }
}
