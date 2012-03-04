package jp.sf.ssoproxy.access.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.access.AccessException;
import jp.sf.ssoproxy.access.AccessManager;
import jp.sf.ssoproxy.access.ResponseHeader;
import jp.sf.ssoproxy.access.StoredCookie;
import jp.sf.ssoproxy.builder.RequestBuilder;
import jp.sf.ssoproxy.builder.RequestBuilderChain;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.HostConfig;
import jp.sf.ssoproxy.config.ProxyConfig;
import jp.sf.ssoproxy.forwarder.Forwarder;
import jp.sf.ssoproxy.util.UrlBuilderUtil;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.seasar.framework.container.S2Container;

public class AccessManagerImpl implements AccessManager {
    private static final String LOCATION_HEADER = "Location";

    private static final String TEMP_FILE_SUFFIX = ".temp";

    private static final String RESPONSE_BODY_FILE_PREFIX = "ssoproxy_response";

    private static final String LOGIN_BODY_FILE_PREFIX = "ssoproxy_login";

    private static final String CONTENT_LENGTH = "Content-Length";

    private static final String SET_COOKIE2_HEADER = "Set-Cookie2";

    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_SEPARATOR = ";";

    private static final String CONTENT_TYPE_CHARSET = "charset=";

    private S2Container container;

    private RequestBuilderChain requestBuilderChain;

    private int downloadThreshold = 1 * 1024 * 1024; // 1m

    private HttpClient getHttpClient(HostConfig hostConfig) {
        // TODO multi-thread
        HttpClient httpclient = new HttpClient();
        // Connection Timeout
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(
                hostConfig.getConnectionTimeout());
        // Cookie Policy
        httpclient.getParams().setCookiePolicy(hostConfig.getCookiePolicy());
        // Single Cookie Header
        if (hostConfig.isSingleCookieHeader()) {
            httpclient.getParams().setBooleanParameter(
                    HttpMethodParams.SINGLE_COOKIE_HEADER, true);
        }
        //        httpclient.getParams().setVersion(HttpVersion.HTTP_1_0); // HTTP 1.1 returns a content encoded by gzip.
        // TODO proxy
        return httpclient;
    }

    private List<StoredCookie> getStoredCookieList(HttpSession httpSession,
            String cookieListName) {
        // get cookieMap from session
        List<StoredCookie> storedCookieList = (List<StoredCookie>) httpSession
                .getAttribute(cookieListName);
        if (storedCookieList == null) {
            //TODO mt-safe?
            storedCookieList = new ArrayList<StoredCookie>();
            httpSession.setAttribute(cookieListName, storedCookieList);
        }
        return storedCookieList;
    }

    private void setCookiesToServer(HttpSession httpSession,
            String cookieListName, HttpClient httpClient) {

        // get storedCookieList from session
        List<StoredCookie> storedCookieList = getStoredCookieList(httpSession,
                cookieListName);

        // cookies
        HttpState initialState = new HttpState();
        for (StoredCookie entry : storedCookieList) {
            initialState.addCookie(entry.getHttpClientCookie());
        }
        httpClient.setState(initialState);
    }

    private void setCookiesToProxy(HttpSession httpSession,
            org.apache.commons.httpclient.Cookie[] httpClientCookies,
            String cookieListName) {

        // get storedCookieList from session
        List<StoredCookie> storedCookieList = getStoredCookieList(httpSession,
                cookieListName);

        for (int i = 0; i < httpClientCookies.length; i++) {
            storedCookieList.add(new StoredCookie(httpClientCookies[i]));
        }
    }

    private void setResponseBody(Map<String, Object> resultMap,
            InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }

        File responseBodyFile = File.createTempFile(RESPONSE_BODY_FILE_PREFIX,
                TEMP_FILE_SUFFIX);
        resultMap.put(SSOProxyConstants.RESPONSE_BODY_FILE_PARAM,
                responseBodyFile);

        DeferredFileOutputStream dfos = new DeferredFileOutputStream(
                getDownloadThreshold(), responseBodyFile);
        IOUtils.copy(inputStream, dfos);
        if (!dfos.isThresholdExceeded()) {
            resultMap.put(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM,
                    new ByteArrayInputStream(dfos.getData()));
            return;
        }
        dfos.flush();
        IOUtils.closeQuietly(dfos);

        resultMap.put(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM,
                new FileInputStream(responseBodyFile));
    }

    private void setLoginResponseBody(Map<String, Object> resultMap,
            InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }

        File responseBodyFile = File.createTempFile(RESPONSE_BODY_FILE_PREFIX,
                TEMP_FILE_SUFFIX);
        resultMap.put(SSOProxyConstants.RESPONSE_BODY_FILE_PARAM,
                responseBodyFile);

        File loginBodyFile = File.createTempFile(LOGIN_BODY_FILE_PREFIX,
                TEMP_FILE_SUFFIX);
        resultMap.put(SSOProxyConstants.LOGIN_BODY_FILE_PARAM, loginBodyFile);

        DeferredFileOutputStream dfos = new DeferredFileOutputStream(
                getDownloadThreshold(), responseBodyFile);
        IOUtils.copy(inputStream, dfos);
        if (!dfos.isThresholdExceeded()) {
            resultMap.put(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM,
                    new ByteArrayInputStream(dfos.getData()));
            resultMap.put(SSOProxyConstants.LOGIN_BODY_INPUT_STREAM_PARAM,
                    new ByteArrayInputStream(dfos.getData()));
            return;
        }
        dfos.flush();
        IOUtils.closeQuietly(dfos);

        resultMap.put(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM,
                new FileInputStream(responseBodyFile));
        resultMap.put(SSOProxyConstants.LOGIN_BODY_INPUT_STREAM_PARAM,
                new FileInputStream(loginBodyFile));
    }

    private Map<String, Object> buildResult(String url, HttpMethod httpMethod,
            int result) throws IOException {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        resultMap.put(SSOProxyConstants.STATUS_CODE_PARAM, result);
        Header[] responseHeaders = httpMethod.getResponseHeaders();
        List<ResponseHeader> responseHeaderList = new ArrayList<ResponseHeader>();
        for (int i = 0; i < responseHeaders.length; i++) {
            responseHeaderList.add(new ResponseHeader(responseHeaders[i]));
        }
        resultMap.put(SSOProxyConstants.RESPONSE_HEADERS_PARAM,
                responseHeaderList);
        // TODO response footer?
        // content type
        Header contentTypeHeader = httpMethod.getResponseHeader(CONTENT_TYPE);
        String mimeType = null;
        String inputEncoding = null;
        if (contentTypeHeader != null) {
            String contentType = contentTypeHeader.getValue();
            if (contentType != null) {
                int pos = contentType.indexOf(CONTENT_TYPE_SEPARATOR);
                if (pos < 0) {
                    mimeType = contentType;
                } else {
                    mimeType = contentType.substring(0, pos);
                    int charsetPos = contentType.indexOf(CONTENT_TYPE_CHARSET);
                    if (charsetPos > 0) {
                        inputEncoding = contentType.substring(charsetPos
                                + CONTENT_TYPE_CHARSET.length());
                    }
                }
            }
        }
        if (mimeType == null) {
            mimeType = DEFAULT_CONTENT_TYPE;
        }
        resultMap.put(SSOProxyConstants.MIME_TYPE_PARAM, mimeType);
        resultMap.put(SSOProxyConstants.URL_PARAM, url);
        resultMap.put(SSOProxyConstants.CONTENT_TYPE_ENCODING_PARAM,
                inputEncoding);
        if (inputEncoding == null) {
            inputEncoding = httpMethod.getParams().getContentCharset();
        }
        resultMap.put(SSOProxyConstants.INPUT_ENCODING_PARAM, inputEncoding);

        return resultMap;
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.access.AccessManager#sendRequest(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String, Object> sendRequest(HttpServletRequest request,
            HostConfig hostConfig, String url, String encoding)
            throws AccessException {

        HttpClient httpclient = getHttpClient(hostConfig);

        String cookieMapName = SSOProxyConstants.STORED_COOKIE_LIST
                + hostConfig.getName();

        HttpSession httpSession = request.getSession();
        setCookiesToServer(httpSession, cookieMapName, httpclient);

        HttpMethod httpMethod = null;
        if (SSOProxyConstants.GET_METHOD.equals(request.getMethod())) {
            httpMethod = UrlBuilderUtil.buildGetMethod(url, request
                    .getParameterMap(), encoding);
        } else if (SSOProxyConstants.POST_METHOD.equals(request.getMethod())) {
            httpMethod = UrlBuilderUtil.buildPostMethod(url, request
                    .getParameterMap(), encoding);
        } else {
            //TODO throw exception
            throw new AccessException("TODO.msg");
        }

        // build request
        requestBuilderChain.reset();
        requestBuilderChain.build(RequestBuilder.DEFAULT_PROCESS, request,
                hostConfig, httpMethod);

        try {
            // execute method
            int result = httpclient.executeMethod(httpMethod);

            // update cookies
            setCookiesToProxy(httpSession, httpclient.getState().getCookies(),
                    cookieMapName);

            Map<String, Object> resultMap = buildResult(url, httpMethod, result);
            setResponseBody(resultMap, httpMethod.getResponseBodyAsStream());
            return resultMap;
        } catch (Exception e) {
            // } catch (HttpException e) {
            // } catch (IOException e) {
            // TODO message
            throw new AccessException("TODO.msg", e);
        } finally {
            httpMethod.releaseConnection();
        }
    }

    public Map<String, Object> sendLoginPageRequest(HttpServletRequest request,
            HostConfig hostConfig, AuthConfig authConfig)
            throws AccessException {

        HttpClient httpclient = getHttpClient(hostConfig);

        String cookieListName = SSOProxyConstants.STORED_COOKIE_LIST
                + hostConfig.getName();

        HttpSession httpSession = request.getSession();
        setCookiesToServer(httpSession, cookieListName, httpclient);

        HttpMethod httpMethod = null;
        try {
            httpMethod = authConfig.buildLoginHttpMethod(request);

            // build request
            requestBuilderChain.reset();
            requestBuilderChain.build(RequestBuilder.LOGIN_PROCESS, request,
                    hostConfig, httpMethod);

            // execute method
            int result = httpclient.executeMethod(httpMethod);

            // update cookies
            setCookiesToProxy(httpSession, httpclient.getState().getCookies(),
                    cookieListName);

            Map<String, Object> resultMap = buildResult(httpMethod.getURI()
                    .toString(), httpMethod, result);
            setLoginResponseBody(resultMap, httpMethod
                    .getResponseBodyAsStream());
            return resultMap;
        } catch (Exception e) {
            // } catch (HttpException e) {
            // } catch (IOException e) {
            // error
            throw new AccessException("000016", new Object[] { hostConfig
                    .getName() }, e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    public Map<String, Object> sendAuthPageRequest(HttpServletRequest request,
            HostConfig hostConfig, AuthConfig authConfig)
            throws AccessException {

        HttpClient httpclient = getHttpClient(hostConfig);

        String cookieMapName = SSOProxyConstants.STORED_COOKIE_LIST
                + hostConfig.getName();

        HttpSession httpSession = request.getSession();
        setCookiesToServer(httpSession, cookieMapName, httpclient);

        HttpMethod httpMethod = null;
        try {
            httpMethod = authConfig.buildAuthHttpMethod(request);

            // build request
            requestBuilderChain.reset();
            requestBuilderChain.build(RequestBuilder.AUTH_PROCESS, request,
                    hostConfig, httpMethod);

            // execute method
            int result = httpclient.executeMethod(httpMethod);

            // update cookies
            setCookiesToProxy(httpSession, httpclient.getState().getCookies(),
                    cookieMapName);

            Map<String, Object> resultMap = buildResult(httpMethod.getURI()
                    .toString(), httpMethod, result);
            setResponseBody(resultMap, httpMethod.getResponseBodyAsStream());
            return resultMap;
        } catch (Exception e) {
            // } catch (HttpException e) {
            // } catch (IOException e) {
            // error
            throw new AccessException("000017", new Object[] { hostConfig
                    .getName() }, e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.access.AccessManager#sendResponse(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.Map, java.lang.String, java.lang.String)
     */
    public void sendResponse(HttpServletRequest request,
            HttpServletResponse response, Map<String, Object> resultMap,
            ProxyConfig proxyConfig, String hostConfigName, String forwarderName)
            throws AccessException {
        // content type
        String mimeType = (String) resultMap
                .get(SSOProxyConstants.MIME_TYPE_PARAM);
        String contentTypeEncoding = (String) resultMap
                .get(SSOProxyConstants.CONTENT_TYPE_ENCODING_PARAM);
        StringBuilder contentType = new StringBuilder(mimeType);
        if (contentTypeEncoding != null) {
            contentType.append("; charset=").append(contentTypeEncoding);
        }
        response.setContentType(contentType.toString());

        // headers
        List<ResponseHeader> responseHeaderList = (List<ResponseHeader>) resultMap
                .get(SSOProxyConstants.RESPONSE_HEADERS_PARAM);
        for (ResponseHeader responseHeader : responseHeaderList) {
            if (SET_COOKIE_HEADER.equals(responseHeader.getName())
                    || SET_COOKIE2_HEADER.equals(responseHeader.getName())) {
                //TODO needed?
            } else if (CONTENT_TYPE.equals(responseHeader.getName())) {
                // nothing
            } else if (CONTENT_LENGTH.equals(responseHeader.getName())) {
                // nothing
            } else {
                //TODO replace
                response.addHeader(responseHeader.getName(), responseHeader
                        .getValue());
            }
        }

        Forwarder forwarder = (Forwarder) container.getComponent(forwarderName);
        if (forwarder == null) {
            // error
            throw new AccessException("000018", new Object[] { forwarderName,
                    hostConfigName });
        }

        Map<String, Object> props = new HashMap<String, Object>();
        props.put(SSOProxyConstants.URL_PARAM, resultMap
                .get(SSOProxyConstants.URL_PARAM));
        props.put(SSOProxyConstants.INPUT_ENCODING_PARAM, resultMap
                .get(SSOProxyConstants.INPUT_ENCODING_PARAM));
        props.put(SSOProxyConstants.OUTPUT_ENCODING_PARAM, resultMap
                .get(SSOProxyConstants.OUTPUT_ENCODING_PARAM));
        props.put(SSOProxyConstants.PROXY_CONFIG_PARAM, proxyConfig);
        //        props.put(SSOProxyConstraints.REQUEST_PARAM, request);
        //        props.put(SSOProxyConstraints.RESPONSE_PARAM, response);

        InputStream inputStream = (InputStream) resultMap
                .get(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM);
        try {
            if (inputStream != null) {
                forwarder.forward(props, inputStream, response
                        .getOutputStream());
            }
        } catch (Exception e) {
            //        } catch (SSOProxyException e) {
            //        } catch (IOException e) {
            // error
            throw new AccessException("000019", new Object[] { forwarderName,
                    hostConfigName }, e);
        }

    }

    public void redirectResponse(HttpServletRequest request,
            HttpServletResponse response, Map<String, Object> resultMap,
            ProxyConfig proxyConfig) throws AccessException {
        String redirectLocation;
        redirectLocation = getResponseHeaderValue(
                (List<ResponseHeader>) resultMap
                        .get(SSOProxyConstants.RESPONSE_HEADERS_PARAM),
                LOCATION_HEADER);
        if (redirectLocation != null) {
            // TODO headers
            try {
                response.setStatus((Integer) resultMap
                        .get(SSOProxyConstants.STATUS_CODE_PARAM));
                response.sendRedirect(proxyConfig
                        .buildProxyUrl(redirectLocation));
            } catch (IOException e) {
                // redirect failed
                // error
                throw new AccessException("000020",
                        new Object[] { redirectLocation }, e);
            }
        } else {
            throw new AccessException("TODO.msg");
        }
    }

    private String getResponseHeaderValue(
            List<ResponseHeader> responseHeaderList, String key) {
        for (ResponseHeader responseHeader : responseHeaderList) {
            if (responseHeader.getName().equals(key)) {
                return responseHeader.getValue();
            }
        }
        return null;
    }

    public void release(Map<String, Object> resultMap) {
        if (resultMap == null) {
            return;
        }

        InputStream responseBodyInputStream = (InputStream) resultMap
                .get(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM);
        if (responseBodyInputStream != null) {
            IOUtils.closeQuietly(responseBodyInputStream);
        }

        File responseBodyFile = (File) resultMap
                .get(SSOProxyConstants.RESPONSE_BODY_FILE_PARAM);
        if (responseBodyFile != null) {
            FileUtils.deleteQuietly(responseBodyFile);
        }

        InputStream loginBodyInputStream = (InputStream) resultMap
                .get(SSOProxyConstants.LOGIN_BODY_INPUT_STREAM_PARAM);
        if (loginBodyInputStream != null) {
            IOUtils.closeQuietly(loginBodyInputStream);
        }

        File loginBodyFile = (File) resultMap
                .get(SSOProxyConstants.LOGIN_BODY_FILE_PARAM);
        if (loginBodyFile != null) {
            FileUtils.deleteQuietly(loginBodyFile);
        }

    }

    public S2Container getContainer() {
        return container;
    }

    public void setContainer(S2Container container) {
        this.container = container;
    }

    public int getDownloadThreshold() {
        return downloadThreshold;
    }

    public void setDownloadThreshold(int downloadThreshold) {
        this.downloadThreshold = downloadThreshold;
    }

    public RequestBuilderChain getRequestBuilderChain() {
        return requestBuilderChain;
    }

    public void setRequestBuilderChain(RequestBuilderChain requestBuilderChain) {
        this.requestBuilderChain = requestBuilderChain;
    }

}
