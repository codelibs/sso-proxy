package jp.sf.ssoproxy.forwarder.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.SSOProxyException;
import jp.sf.ssoproxy.forwarder.Forwarder;
import jp.sf.ssoproxy.forwarder.ForwarderException;
import jp.sf.ssoproxy.handler.html.HtmlHandler;

import org.cyberneko.html.parsers.SAXParser;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.log.Logger;
import org.xml.sax.InputSource;

public class HtmlForwarder implements Forwarder {
    private static final String DEFAULT_HTML_HANDLER_NAME = "defaultHtmlHandler";

    private static final String DEFAULT_INPUT_ENCODING = "UTF-8";

    private static final Logger logger = Logger.getLogger(HtmlForwarder.class);

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
        //        htmlHandler.getProperties().put(SSOProxyConstraints.REQUEST_PARAM,
        //                (String) props.get(SSOProxyConstraints.REQUEST_PARAM));
        //        htmlHandler.getProperties().put(SSOProxyConstraints.RESPONSE_PARAM,
        //                (String) props.get(SSOProxyConstraints.RESPONSE_PARAM));

        InputSource inputSource = new InputSource(is);
        inputSource.setEncoding(inputEncoding);
        // for debug
        //        InputSource inputSource;
        //        try {
        //            BufferedReader reader = new BufferedReader(new InputStreamReader(
        //                    is, inputEncoding));
        //            System.out.println("TEST: " + reader.readLine());
        //            System.out.println("TEST: " + reader.readLine());
        //            System.out.println("TEST: " + reader.readLine());
        //            inputSource = new InputSource(reader);
        //        } catch (UnsupportedEncodingException e1) {
        //            inputSource = new InputSource(is);
        //            inputSource.setEncoding(inputEncoding);
        //        } catch (IOException e1) {
        //            inputSource = new InputSource(is);
        //            inputSource.setEncoding(inputEncoding);
        //        }

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
            if (logger.isDebugEnabled()) {
                logger.debug("content=" + htmlHandler.toString());
            }
            osw.flush();
        } catch (Exception e) {
            // } catch (SAXNotRecognizedException e) {
            // } catch (SAXNotSupportedException e) {
            // } catch (UnsupportedEncodingException e) {
            // } catch (SAXException e) {
            // } catch (IOException e) {
            //TODO msg
            throw new ForwarderException("TODO.msg", e);
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
