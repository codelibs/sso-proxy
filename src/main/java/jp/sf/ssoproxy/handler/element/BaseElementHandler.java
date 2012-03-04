package jp.sf.ssoproxy.handler.element;

import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.handler.html.HtmlHandler;
import jp.sf.ssoproxy.util.ElementHandlerUtil;

import org.xml.sax.Attributes;

public class BaseElementHandler extends DefaultElementHandler {

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
                if (ElementHandler.HREF_ATTR.equals(attrName)) {
                    final Map<String, Object> props = htmlHandler
                            .getProperties();
                    // store original url
                    props.put(SSOProxyConstants.ORIGINAL_URL_PARAM,
                            props.get(SSOProxyConstants.URL_PARAM));
                    // replace current url
                    final String url = attributes.getValue(i);
                    props.put(SSOProxyConstants.URL_PARAM, url);
                    htmlHandler.write(ElementHandlerUtil.buildUrl(htmlHandler,
                            url));
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
