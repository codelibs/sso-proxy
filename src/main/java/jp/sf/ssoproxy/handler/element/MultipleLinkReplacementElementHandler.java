package jp.sf.ssoproxy.handler.element;

import java.util.List;

import jp.sf.ssoproxy.handler.html.HtmlHandler;
import jp.sf.ssoproxy.util.ElementHandlerUtil;

import org.xml.sax.Attributes;

public class MultipleLinkReplacementElementHandler extends
        DefaultElementHandler {
    private final List<String> replacedAttrs;

    public MultipleLinkReplacementElementHandler(
            final List<String> replacedAttrs) {
        this.replacedAttrs = replacedAttrs;
    }

    @Override
    public void startElement(final HtmlHandler htmlHandler, final String uri,
            final String localName, final String name,
            final Attributes attributes) {
        if (htmlHandler.isWritable()) {
            htmlHandler.write(OPEN_TAG_PREFIX);
            htmlHandler.write(name.toLowerCase());
            for (int i = 0; i < attributes.getLength(); i++) {
                final String attrName = attributes.getQName(i).toLowerCase();
                htmlHandler.write(SPACE);
                htmlHandler.write(attrName);
                htmlHandler.write(ATTR_VALUE_EQUAL);
                htmlHandler.write(getQuotationMark());
                if (replacedAttrs.contains(attrName)) {
                    htmlHandler.write(ElementHandlerUtil.buildUrl(htmlHandler,
                            attributes.getValue(i)));
                } else {
                    htmlHandler.write(attributes.getValue(i));
                }
                htmlHandler.write(getQuotationMark());
            }
            if (isEndWithSlash()) {
                htmlHandler.write(SLASH);
            }
            htmlHandler.write(OPEN_TAG_SUFFIX);
        }
    }

}
