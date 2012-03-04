package jp.sf.ssoproxy.forwarder.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.SSOProxyException;
import jp.sf.ssoproxy.forwarder.Forwarder;
import jp.sf.ssoproxy.forwarder.ForwarderException;
import jp.sf.ssoproxy.handler.html.HtmlHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberneko.html.parsers.SAXParser;
import org.seasar.framework.container.S2Container;
import org.xml.sax.InputSource;

public class HtmlForwarder implements Forwarder {

    /**
     * Logger for this class
     */
    private static final Log log = LogFactory.getLog(HtmlForwarder.class);

    private static final String HTML_FEATURES_SCANNER_NOTIFY_BUILTIN_REFS = "http://cyberneko.org/html/features/scanner/notify-builtin-refs";

    private static final String XML_FEATURES_SCANNER_NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";

    private static final String XML_FEATURES_SCANNER_NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";

    private static final String HTML_FEATURES_SCANNER_CDATA_SECTIONS = "http://cyberneko.org/html/features/scanner/cdata-sections";

    private static final String HTML_PROPERTIES_NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";

    private static final String HTML_PROPERTIES_NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";

    private static final String HTML_PROPERTIES_DEFAULT_ENCODING = "http://cyberneko.org/html/properties/default-encoding";

    private static final String DEFAULT_HTML_HANDLER_NAME = "defaultHtmlHandler";

    private static final String DEFAULT_INPUT_ENCODING = "UTF-8";

    private static final String UPPER_CASE_ELEMENT = "upper";

    private static final String LOWER_CASE_ELEMENT = "lower";

    private static final String NO_CHANGE_CASE_ELEMENT = "match";

    private static final String UPPER_CASE_ATTRIBUTE = "upper";

    private static final String LOWER_CASE_ATTRIBUTE = "lower";

    private static final String NO_CHANGE_CASE_ATTRIBUTE = "no-change";

    private S2Container container;

    private String htmlHandlerName;

    private String elementNameCase;

    private String attributeNameCase;

    private boolean xmlNotifyCharRefs;

    private boolean xmlNotifyBuiltinRefs;

    private boolean htmlNotifyBuiltinRefs;

    private boolean cdataSections;

    public HtmlForwarder() {
        elementNameCase = NO_CHANGE_CASE_ELEMENT;
        attributeNameCase = NO_CHANGE_CASE_ATTRIBUTE;
        xmlNotifyCharRefs = false;
        xmlNotifyBuiltinRefs = false;
        htmlNotifyBuiltinRefs = false;
        cdataSections = true;
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.forwarder.impl.Forwarder#forward(java.util.Map, java.io.InputStream, java.io.OutputStream)
     */
    public void forward(Map<String, Object> props, InputStream is,
            OutputStream os) throws SSOProxyException {
        HtmlHandler htmlHandler = (HtmlHandler) getContainer().getComponent(
                getHtmlHandlerName());

        String inputEncoding = (String) props
                .get(SSOProxyConstants.INPUT_ENCODING_PARAM);
        if (inputEncoding == null) {
            inputEncoding = DEFAULT_INPUT_ENCODING;
        }

        // set properties
        htmlHandler.getProperties().put(SSOProxyConstants.URL_PARAM,
                props.get(SSOProxyConstants.URL_PARAM));
        htmlHandler.getProperties().put(SSOProxyConstants.PROXY_CONFIG_PARAM,
                props.get(SSOProxyConstants.PROXY_CONFIG_PARAM));

        InputSource inputSource = new InputSource(is);
        inputSource.setEncoding(inputEncoding);

        try {
            SAXParser parser = new SAXParser();
            parser.setContentHandler(htmlHandler);

            // Features
            parser.setFeature(XML_FEATURES_SCANNER_NOTIFY_CHAR_REFS,
                    xmlNotifyCharRefs);
            parser.setFeature(XML_FEATURES_SCANNER_NOTIFY_BUILTIN_REFS,
                    xmlNotifyBuiltinRefs);
            parser.setFeature(HTML_FEATURES_SCANNER_NOTIFY_BUILTIN_REFS,
                    htmlNotifyBuiltinRefs);
            parser.setFeature(HTML_FEATURES_SCANNER_CDATA_SECTIONS,
                    cdataSections);

            // Properties
            parser.setProperty(HTML_PROPERTIES_DEFAULT_ENCODING, inputEncoding);
            parser.setProperty(HTML_PROPERTIES_NAMES_ELEMS, elementNameCase);
            parser.setProperty(HTML_PROPERTIES_NAMES_ATTRS, attributeNameCase);

            parser.parse(inputSource);

            String encoding = (String) props
                    .get(SSOProxyConstants.OUTPUT_ENCODING_PARAM);
            if (encoding == null) {
                //                encoding = DEFAULT_OUTPUT_ENCODING;
                encoding = inputEncoding;
            }
            OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
            osw.write(htmlHandler.toString());
            if (log.isDebugEnabled()) {
                log.debug("content=" + htmlHandler.toString());
            }
            osw.flush();
        } catch (Exception e) {
            // } catch (SAXNotRecognizedException e) {
            // } catch (SAXNotSupportedException e) {
            // } catch (UnsupportedEncodingException e) {
            // } catch (SAXException e) {
            // } catch (IOException e) {
            // error
            throw new ForwarderException("000006", new Object[] { props
                    .get(SSOProxyConstants.URL_PARAM) }, e);
        }
    }

    public S2Container getContainer() {
        return container;
    }

    public void setContainer(S2Container container) {
        this.container = container;
    }

    public String getHtmlHandlerName() {
        if (htmlHandlerName == null) {
            return DEFAULT_HTML_HANDLER_NAME;
        }
        return htmlHandlerName;
    }

    public void setHtmlHandlerName(String htmlHandlerName) {
        this.htmlHandlerName = htmlHandlerName;
    }

    public String getElementNameCase() {
        return elementNameCase;
    }

    public void setElementNameCase(String elementNameCase) {
        if (UPPER_CASE_ELEMENT.equals(elementNameCase)) {
            this.elementNameCase = UPPER_CASE_ELEMENT;
        } else if (LOWER_CASE_ELEMENT.equals(elementNameCase)) {
            this.elementNameCase = LOWER_CASE_ELEMENT;
        } else if (LOWER_CASE_ELEMENT.equals(elementNameCase)) {
            this.elementNameCase = NO_CHANGE_CASE_ELEMENT;
        } else {
            this.elementNameCase = NO_CHANGE_CASE_ATTRIBUTE;
        }
    }

    public String getAttributeNameCase() {
        return attributeNameCase;
    }

    public void setAttributeNameCase(String attributeNameCase) {
        if (UPPER_CASE_ATTRIBUTE.equals(attributeNameCase)) {
            this.attributeNameCase = UPPER_CASE_ATTRIBUTE;
        } else if (LOWER_CASE_ATTRIBUTE.equals(attributeNameCase)) {
            this.attributeNameCase = LOWER_CASE_ATTRIBUTE;
        } else if (NO_CHANGE_CASE_ATTRIBUTE.equals(attributeNameCase)) {
            this.attributeNameCase = NO_CHANGE_CASE_ATTRIBUTE;
        } else {
            this.attributeNameCase = NO_CHANGE_CASE_ATTRIBUTE;
        }
    }

    public boolean isXmlNotifyCharRefs() {
        return xmlNotifyCharRefs;
    }

    public void setXmlNotifyCharRefs(boolean xmlNotifyCharRefs) {
        this.xmlNotifyCharRefs = xmlNotifyCharRefs;
    }

    public boolean isXmlNotifyBuiltinRefs() {
        return xmlNotifyBuiltinRefs;
    }

    public void setXmlNotifyBuiltinRefs(boolean xmlNotifyBuiltinRefs) {
        this.xmlNotifyBuiltinRefs = xmlNotifyBuiltinRefs;
    }

    public boolean isHtmlNotifyBuiltinRefs() {
        return htmlNotifyBuiltinRefs;
    }

    public void setHtmlNotifyBuiltinRefs(boolean htmlNotifyBuiltinRefs) {
        this.htmlNotifyBuiltinRefs = htmlNotifyBuiltinRefs;
    }

    public boolean isCdataSections() {
        return cdataSections;
    }

    public void setCdataSections(boolean cdataSections) {
        this.cdataSections = cdataSections;
    }
}
