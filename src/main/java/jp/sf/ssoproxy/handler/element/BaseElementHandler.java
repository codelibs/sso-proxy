package jp.sf.ssoproxy.handler.element;

import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.handler.html.HtmlHandler;
import jp.sf.ssoproxy.util.ElementHandlerUtil;

import org.xml.sax.Attributes;

public class BaseElementHandler extends DefaultElementHandler {

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
                if (ElementHandler.HREF_ATTR.equals(attrName)) {
                    Map<String, Object> props = htmlHandler.getProperties();
                    // store original url
                    props.put(SSOProxyConstraints.ORIGINAL_URL_PARAM, props
                            .get(SSOProxyConstraints.URL_PARAM));
                    // replace current url
                    String url = attributes.getValue(i);
                    props.put(SSOProxyConstraints.URL_PARAM, url);
                    htmlHandler.write(ElementHandlerUtil.buildUrl(htmlHandler,
                            url));
                } else {
                    htmlHandler.write(attributes.getValue(i));
                }
                htmlHandler.write(ATTR_VALUE_END);
            }
            htmlHandler.write(OPEN_TAG_SUFFIX);
        }
    }

}
