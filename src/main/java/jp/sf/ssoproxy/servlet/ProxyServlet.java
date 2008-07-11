package jp.sf.ssoproxy.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.access.AccessManager;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.HostConfig;
import jp.sf.ssoproxy.config.ProxyConfig;
import jp.sf.ssoproxy.util.ErrorHandlingUtil;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class ProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 778883232389401884L;

    private static final String PROXY_CONFIG_COMPONENT = "proxyConfig";

    private String errorPage;

    private Locale systemLocale;

    @Override
    public void init() throws ServletException {
        // set an error page
        errorPage = getServletConfig().getInitParameter(
                SSOProxyConstraints.ERROR_JSP_KEY);
        if (errorPage == null) {
            errorPage = SSOProxyConstraints.DEFAULT_ERROR_JSP;
        }
        // set a system locale
        String value = getServletConfig().getInitParameter(
                SSOProxyConstraints.SYSTEM_LOCALE_KEY);
        if (value != null) {
            try {
                String[] values = value.split("_");
                if (values.length == 3) {
                    systemLocale = new Locale(values[0], values[1], values[2]);
                } else if (values.length == 2) {
                    systemLocale = new Locale(values[0], values[1]);
                } else if (values.length == 1) {
                    systemLocale = new Locale(values[0]);
                } else {
                    systemLocale = Locale.ENGLISH;
                }
            } catch (RuntimeException e) {
                systemLocale = Locale.ENGLISH;
            }
        } else {
            systemLocale = Locale.ENGLISH;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // needs to check a user name from session by filter

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            // error
            String erroCode = "000002";
            ErrorHandlingUtil.printLog(erroCode, null, systemLocale);
            ErrorHandlingUtil.forwardErrorPage(request, response, erroCode,
                    null, errorPage);
            return;
        }

        int actualPathPos = pathInfo.indexOf("/", 1);
        if (actualPathPos < 0) {
            // error
            String erroCode = "000003";
            ErrorHandlingUtil.printLog(erroCode, null, systemLocale);
            ErrorHandlingUtil.forwardErrorPage(request, response, erroCode,
                    null, errorPage);
            return;
        }

        S2Container container = SingletonS2ContainerFactory.getContainer();
        // Get a proxy configuration
        ProxyConfig proxyConfig = (ProxyConfig) container
                .getComponent(PROXY_CONFIG_COMPONENT);

        // Get a host configuration by a host config name of a path info
        String hostConfigName = pathInfo.substring(1, actualPathPos);
        HostConfig hostConfig = proxyConfig.getHostConfig(hostConfigName);
        if (hostConfig == null) {
            // error
            String erroCode = "000004";
            ErrorHandlingUtil.printLog(erroCode,
                    new Object[] { hostConfigName }, systemLocale);
            ErrorHandlingUtil.forwardErrorPage(request, response, erroCode,
                    new Object[] { hostConfigName }, errorPage);
            return;
        }

        // Get a access manager by the host config
        AccessManager accessManager = (AccessManager) container
                .getComponent(hostConfig.getAccessManagerName());
        if (accessManager == null) {
            // error
            String erroCode = "000005";
            ErrorHandlingUtil.printLog(erroCode, new Object[] {
                    hostConfig.getAccessManagerName(), hostConfigName },
                    systemLocale);
            ErrorHandlingUtil.forwardErrorPage(request, response, erroCode,
                    new Object[] { hostConfig.getAccessManagerName(),
                            hostConfigName }, errorPage);
            return;
        }

        try {
            // set request encoding
            request.setCharacterEncoding(hostConfig.getEncoding());
        } catch (UnsupportedEncodingException e) {
            // error
            ErrorHandlingUtil.printLog(e, systemLocale);
            ErrorHandlingUtil.forwardErrorPage(request, response, e, errorPage);
            return;
        }

        Map<String, Object> resultMap = null;
        try {
            String url = hostConfig.buildUrl(pathInfo.substring(actualPathPos));
            // TODO the url has request parameters or not when checking it in a following method...
            AuthConfig authConfig = hostConfig.getAuthConfig(request
                    .getMethod(), url, request.getParameterMap());

            //  check a login page
            if (authConfig != null) {
                // Check a login page
                resultMap = accessManager.sendLoginPageRequest(request,
                        hostConfig, authConfig);
                int statusCode = ((Integer) resultMap
                        .get(SSOProxyConstraints.STATUS_CODE_PARAM)).intValue();
                if (statusCode == 200
                        && authConfig
                                .checkLoginPage((InputStream) resultMap
                                        .get(SSOProxyConstraints.LOGIN_BODY_INPUT_STREAM_PARAM))) {
                    // release result
                    accessManager.release(resultMap);
                    // Send auth info
                    resultMap = accessManager.sendAuthPageRequest(request,
                            hostConfig, authConfig);
                }
            } else {
                // get a content from a back-end server
                resultMap = accessManager.sendRequest(request, hostConfig, url,
                        hostConfig.getEncoding());
            }

            int statusCode = ((Integer) resultMap
                    .get(SSOProxyConstraints.STATUS_CODE_PARAM)).intValue();
            // redirect
            if (isRedirectStatusCode(statusCode)) {
                accessManager.redirectResponse(request, response, resultMap,
                        proxyConfig);
                return;
            }

            // TODO error

            // set the status to a response
            response.setStatus(statusCode);

            // store a content to a client
            accessManager.sendResponse(request, response, resultMap,
                    proxyConfig, hostConfigName, hostConfig
                            .getForwarderName((String) resultMap
                                    .get(SSOProxyConstraints.MIME_TYPE_PARAM)));
            // flush
            response.flushBuffer();
        } catch (Exception e) {
            // error
            ErrorHandlingUtil.printLog(e, systemLocale);
            //TODO error
            ErrorHandlingUtil.forwardErrorPage(request, response, e, errorPage);
            return;
        } finally {
            accessManager.release(resultMap);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private boolean isRedirectStatusCode(int statusCode) {
        switch (statusCode) {
        case 301:
        case 302:
        case 303:
        case 307:
            return true;

        case 304:
        case 305:
        case 306:
        default:
            return false;
        }
    }
}
