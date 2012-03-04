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

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.seasar.framework.container.SingletonS2Container;

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

    private RequestBuilderChain requestBuilderChain;

    private int downloadThreshold = 1 * 1024 * 1024; // 1m

    private List<StoredCookie> getStoredCookieList(
            final HttpSession httpSession, final String cookieListName) {
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

    private void setCookiesToServer(final HttpSession httpSession,
            final String cookieListName, final HttpClient httpClient) {

        // get storedCookieList from session
        final List<StoredCookie> storedCookieList = getStoredCookieList(
                httpSession, cookieListName);

        // cookies
        final HttpState initialState = new HttpState();
        for (final StoredCookie entry : storedCookieList) {
            initialState.addCookie(entry.getHttpClientCookie());
        }
        httpClient.setState(initialState);
    }

    private void setCookiesToProxy(final HttpSession httpSession,
            final org.apache.commons.httpclient.Cookie[] httpClientCookies,
            final String cookieListName) {

        // get storedCookieList from session
        final List<StoredCookie> storedCookieList = getStoredCookieList(
                httpSession, cookieListName);

        for (final Cookie httpClientCookie : httpClientCookies) {
            storedCookieList.add(new StoredCookie(httpClientCookie));
        }
    }

    private void setResponseBody(final Map<String, Object> resultMap,
            final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }

        final File responseBodyFile = File.createTempFile(
                RESPONSE_BODY_FILE_PREFIX, TEMP_FILE_SUFFIX);
        resultMap.put(SSOProxyConstants.RESPONSE_BODY_FILE_PARAM,
                responseBodyFile);

        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(
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

    private void setLoginResponseBody(final Map<String, Object> resultMap,
            final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }

        final File responseBodyFile = File.createTempFile(
                RESPONSE_BODY_FILE_PREFIX, TEMP_FILE_SUFFIX);
        resultMap.put(SSOProxyConstants.RESPONSE_BODY_FILE_PARAM,
                responseBodyFile);

        final File loginBodyFile = File.createTempFile(LOGIN_BODY_FILE_PREFIX,
                TEMP_FILE_SUFFIX);
        resultMap.put(SSOProxyConstants.LOGIN_BODY_FILE_PARAM, loginBodyFile);

        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(
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

    private Map<String, Object> buildResult(final String url,
            final HttpMethod httpMethod, final int result) throws IOException {
        final Map<String, Object> resultMap = new HashMap<String, Object>();

        resultMap.put(SSOProxyConstants.STATUS_CODE_PARAM, result);
        final Header[] responseHeaders = httpMethod.getResponseHeaders();
        final List<ResponseHeader> responseHeaderList = new ArrayList<ResponseHeader>();
        for (final Header responseHeader : responseHeaders) {
            responseHeaderList.add(new ResponseHeader(responseHeader));
        }
        resultMap.put(SSOProxyConstants.RESPONSE_HEADERS_PARAM,
                responseHeaderList);
        // TODO response footer?
        // content type
        final Header contentTypeHeader = httpMethod
                .getResponseHeader(CONTENT_TYPE);
        String mimeType = null;
        String inputEncoding = null;
        if (contentTypeHeader != null) {
            final String contentType = contentTypeHeader.getValue();
            if (contentType != null) {
                final int pos = contentType.indexOf(CONTENT_TYPE_SEPARATOR);
                if (pos < 0) {
                    mimeType = contentType;
                } else {
                    mimeType = contentType.substring(0, pos);
                    final int charsetPos = contentType
                            .indexOf(CONTENT_TYPE_CHARSET);
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
    @Override
    public Map<String, Object> sendRequest(final HttpServletRequest request,
            final HostConfig hostConfig, final String url, final String encoding) {

        final HttpClient httpclient = hostConfig.getHttpClient();

        final String cookieMapName = SSOProxyConstants.STORED_COOKIE_LIST
                + hostConfig.getName();

        final HttpSession httpSession = request.getSession();
        setCookiesToServer(httpSession, cookieMapName, httpclient);

        HttpMethod httpMethod = null;
        if (SSOProxyConstants.GET_METHOD.equals(request.getMethod())) {
            httpMethod = UrlBuilderUtil.buildGetMethod(url,
                    request.getParameterMap(), encoding);
        } else if (SSOProxyConstants.POST_METHOD.equals(request.getMethod())) {
            httpMethod = UrlBuilderUtil.buildPostMethod(url,
                    request.getParameterMap(), encoding);
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
            final int result = httpclient.executeMethod(httpMethod);

            // update cookies
            setCookiesToProxy(httpSession, httpclient.getState().getCookies(),
                    cookieMapName);

            final Map<String, Object> resultMap = buildResult(url, httpMethod,
                    result);
            setResponseBody(resultMap, httpMethod.getResponseBodyAsStream());
            return resultMap;
        } catch (final Exception e) {
            // } catch (HttpException e) {
            // } catch (IOException e) {
            // TODO message
            throw new AccessException("TODO.msg", e);
        } finally {
            httpMethod.releaseConnection();
        }
    }

    @Override
    public Map<String, Object> sendLoginPageRequest(
            final HttpServletRequest request, final HostConfig hostConfig,
            final AuthConfig authConfig) {

        final HttpClient httpclient = hostConfig.getHttpClient();

        final String cookieListName = SSOProxyConstants.STORED_COOKIE_LIST
                + hostConfig.getName();

        final HttpSession httpSession = request.getSession();
        setCookiesToServer(httpSession, cookieListName, httpclient);

        HttpMethod httpMethod = null;
        try {
            httpMethod = authConfig.buildLoginHttpMethod(request);

            // build request
            requestBuilderChain.reset();
            requestBuilderChain.build(RequestBuilder.LOGIN_PROCESS, request,
                    hostConfig, httpMethod);

            // execute method
            final int result = httpclient.executeMethod(httpMethod);

            // update cookies
            setCookiesToProxy(httpSession, httpclient.getState().getCookies(),
                    cookieListName);

            final Map<String, Object> resultMap = buildResult(httpMethod
                    .getURI().toString(), httpMethod, result);
            setLoginResponseBody(resultMap,
                    httpMethod.getResponseBodyAsStream());
            return resultMap;
        } catch (final Exception e) {
            // } catch (HttpException e) {
            // } catch (IOException e) {
            // error
            throw new AccessException("000016",
                    new Object[] { hostConfig.getName() }, e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    @Override
    public Map<String, Object> sendAuthPageRequest(
            final HttpServletRequest request, final HostConfig hostConfig,
            final AuthConfig authConfig) {

        final HttpClient httpclient = hostConfig.getHttpClient();

        final String cookieMapName = SSOProxyConstants.STORED_COOKIE_LIST
                + hostConfig.getName();

        final HttpSession httpSession = request.getSession();
        setCookiesToServer(httpSession, cookieMapName, httpclient);

        HttpMethod httpMethod = null;
        try {
            httpMethod = authConfig.buildAuthHttpMethod(request);

            // build request
            requestBuilderChain.reset();
            requestBuilderChain.build(RequestBuilder.AUTH_PROCESS, request,
                    hostConfig, httpMethod);

            // execute method
            final int result = httpclient.executeMethod(httpMethod);

            // update cookies
            setCookiesToProxy(httpSession, httpclient.getState().getCookies(),
                    cookieMapName);

            final Map<String, Object> resultMap = buildResult(httpMethod
                    .getURI().toString(), httpMethod, result);
            setResponseBody(resultMap, httpMethod.getResponseBodyAsStream());
            return resultMap;
        } catch (final Exception e) {
            // error
            throw new AccessException("000017",
                    new Object[] { hostConfig.getName() }, e);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.access.AccessManager#sendResponse(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.Map, java.lang.String, java.lang.String)
     */
    @Override
    public void sendResponse(final HttpServletRequest request,
            final HttpServletResponse response,
            final Map<String, Object> resultMap, final ProxyConfig proxyConfig,
            final String hostConfigName, final String forwarderName) {
        // content type
        final String mimeType = (String) resultMap
                .get(SSOProxyConstants.MIME_TYPE_PARAM);
        final String contentTypeEncoding = (String) resultMap
                .get(SSOProxyConstants.CONTENT_TYPE_ENCODING_PARAM);
        final StringBuilder contentType = new StringBuilder(mimeType);
        if (contentTypeEncoding != null) {
            contentType.append("; charset=").append(contentTypeEncoding);
        }
        response.setContentType(contentType.toString());

        // headers
        final List<ResponseHeader> responseHeaderList = (List<ResponseHeader>) resultMap
                .get(SSOProxyConstants.RESPONSE_HEADERS_PARAM);
        for (final ResponseHeader responseHeader : responseHeaderList) {
            if (SET_COOKIE_HEADER.equals(responseHeader.getName())
                    || SET_COOKIE2_HEADER.equals(responseHeader.getName())) {
                //TODO needed?
            } else if (CONTENT_TYPE.equals(responseHeader.getName())) {
                // nothing
            } else if (CONTENT_LENGTH.equals(responseHeader.getName())) {
                // nothing
            } else {
                //TODO replace
                response.addHeader(responseHeader.getName(),
                        responseHeader.getValue());
            }
        }

        final Forwarder forwarder = SingletonS2Container
                .getComponent(forwarderName);
        if (forwarder == null) {
            // error
            throw new AccessException("000018", new Object[] { forwarderName,
                    hostConfigName });
        }

        final Map<String, Object> props = new HashMap<String, Object>();
        props.put(SSOProxyConstants.URL_PARAM,
                resultMap.get(SSOProxyConstants.URL_PARAM));
        props.put(SSOProxyConstants.INPUT_ENCODING_PARAM,
                resultMap.get(SSOProxyConstants.INPUT_ENCODING_PARAM));
        props.put(SSOProxyConstants.OUTPUT_ENCODING_PARAM,
                resultMap.get(SSOProxyConstants.OUTPUT_ENCODING_PARAM));
        props.put(SSOProxyConstants.PROXY_CONFIG_PARAM, proxyConfig);
        //        props.put(SSOProxyConstraints.REQUEST_PARAM, request);
        //        props.put(SSOProxyConstraints.RESPONSE_PARAM, response);

        final InputStream inputStream = (InputStream) resultMap
                .get(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM);
        try {
            if (inputStream != null) {
                forwarder.forward(props, inputStream,
                        response.getOutputStream());
            }
        } catch (final Exception e) {
            //        } catch (SSOProxyException e) {
            //        } catch (IOException e) {
            // error
            throw new AccessException("000019", new Object[] { forwarderName,
                    hostConfigName }, e);
        }

    }

    @Override
    public void redirectResponse(final HttpServletRequest request,
            final HttpServletResponse response,
            final Map<String, Object> resultMap, final ProxyConfig proxyConfig) {
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
            } catch (final IOException e) {
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
            final List<ResponseHeader> responseHeaderList, final String key) {
        for (final ResponseHeader responseHeader : responseHeaderList) {
            if (responseHeader.getName().equals(key)) {
                return responseHeader.getValue();
            }
        }
        return null;
    }

    @Override
    public void release(final Map<String, Object> resultMap) {
        if (resultMap == null) {
            return;
        }

        final InputStream responseBodyInputStream = (InputStream) resultMap
                .get(SSOProxyConstants.RESPONSE_BODY_INPUT_STREAM_PARAM);
        if (responseBodyInputStream != null) {
            IOUtils.closeQuietly(responseBodyInputStream);
        }

        final File responseBodyFile = (File) resultMap
                .get(SSOProxyConstants.RESPONSE_BODY_FILE_PARAM);
        if (responseBodyFile != null) {
            FileUtils.deleteQuietly(responseBodyFile);
        }

        final InputStream loginBodyInputStream = (InputStream) resultMap
                .get(SSOProxyConstants.LOGIN_BODY_INPUT_STREAM_PARAM);
        if (loginBodyInputStream != null) {
            IOUtils.closeQuietly(loginBodyInputStream);
        }

        final File loginBodyFile = (File) resultMap
                .get(SSOProxyConstants.LOGIN_BODY_FILE_PARAM);
        if (loginBodyFile != null) {
            FileUtils.deleteQuietly(loginBodyFile);
        }

    }

    public int getDownloadThreshold() {
        return downloadThreshold;
    }

    public void setDownloadThreshold(final int downloadThreshold) {
        this.downloadThreshold = downloadThreshold;
    }

    public RequestBuilderChain getRequestBuilderChain() {
        return requestBuilderChain;
    }

    public void setRequestBuilderChain(
            final RequestBuilderChain requestBuilderChain) {
        this.requestBuilderChain = requestBuilderChain;
    }

}
