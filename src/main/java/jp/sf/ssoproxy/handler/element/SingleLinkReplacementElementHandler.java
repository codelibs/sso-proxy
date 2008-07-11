package jp.sf.ssoproxy.handler.element;

import jp.sf.ssoproxy.handler.html.HtmlHandler;
import jp.sf.ssoproxy.util.ElementHandlerUtil;

import org.xml.sax.Attributes;

public class SingleLinkReplacementElementHandler extends DefaultElementHandler {
    private String replacedAttr;

    public SingleLinkReplacementElementHandler(String replacedAttr) {
        this.replacedAttr = replacedAttr;
    }

    public void startElement(HtmlHandler htmlHandler, String uri,
            String localName, String name, Attributes attributes) {
        if (htmlHandler.isWritable()) {
            htmlHandler.write(OPEN_TAG_PREFIX);
            htmlHandler.write(name.toLowerCase());
            for (int i = 0; i < attributes.getLength(); i++) {
                String attrName = attributes.getQName(i).toLowerCase();
                htmlHandler.write(SPACE);
                htmlHandler.write(attrName);
                htmlHandler.write(ATTR_VALUE_EQUAL);
                htmlHandler.write(getQuotationMark());
                if (replacedAttr.equals(attrName)) {
                    htmlHandler.write(ElementHandlerUtil.buildUrl(htmlHandler,
                            attributes.getValue(i)));
                } else {
                    htmlHandler.write(attributes.getValue(i));
                }
                htmlHandler.write(getQuotationMark());
            }
            htmlHandler.write(OPEN_TAG_SUFFIX);
        }
    }

}
