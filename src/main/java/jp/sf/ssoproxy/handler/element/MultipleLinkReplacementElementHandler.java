package jp.sf.ssoproxy.handler.element;

import java.util.List;

import jp.sf.ssoproxy.handler.html.HtmlHandler;
import jp.sf.ssoproxy.util.ElementHandlerUtil;

import org.xml.sax.Attributes;

public class MultipleLinkReplacementElementHandler extends
        DefaultElementHandler {
    private List<String> replacedAttrs;

    public MultipleLinkReplacementElementHandler(List<String> replacedAttrs) {
        this.replacedAttrs = replacedAttrs;
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
                htmlHandler.write(ATTR_VALUE_BEGIN);
                if (replacedAttrs.contains(attrName)) {
                    htmlHandler.write(ElementHandlerUtil.buildUrl(htmlHandler,
                            attributes.getValue(i)));
                } else {
                    htmlHandler.write(attributes.getValue(i));
                }
                htmlHandler.write(ATTR_VALUE_END);
            }
            htmlHandler.write(OPEN_TAG_SUFFIX);
        }
    }

}
