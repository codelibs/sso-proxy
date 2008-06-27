package jp.sf.ssoproxy.forwarder.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstraints;
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

    private static final String DEFAULT_HTML_HANDLER_NAME = "defaultHtmlHandler";

    private static final String DEFAULT_INPUT_ENCODING = "UTF-8";

    private S2Container container;

    private String htmlHandlerName;

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.forwarder.impl.Forwarder#forward(java.util.Map, java.io.InputStream, java.io.OutputStream)
     */
    public void forward(Map<String, Object> props, InputStream is,
            OutputStream os) throws SSOProxyException {
        HtmlHandler htmlHandler = (HtmlHandler) getContainer().getComponent(
                getHtmlHandlerName());

        String inputEncoding = (String) props
                .get(SSOProxyConstraints.INPUT_ENCODING_PARAM);
        if (inputEncoding == null) {
            inputEncoding = DEFAULT_INPUT_ENCODING;
        }

        // set properties
        htmlHandler.getProperties().put(SSOProxyConstraints.URL_PARAM,
                props.get(SSOProxyConstraints.URL_PARAM));
        htmlHandler.getProperties().put(SSOProxyConstraints.PROXY_CONFIG_PARAM,
                props.get(SSOProxyConstraints.PROXY_CONFIG_PARAM));

        InputSource inputSource = new InputSource(is);
        inputSource.setEncoding(inputEncoding);

        try {
            SAXParser parser = new SAXParser();
            parser.setContentHandler(htmlHandler);
            parser.parse(inputSource);

            String encoding = (String) props
                    .get(SSOProxyConstraints.OUTPUT_ENCODING_PARAM);
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
                    .get(SSOProxyConstraints.URL_PARAM) }, e);
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
}
