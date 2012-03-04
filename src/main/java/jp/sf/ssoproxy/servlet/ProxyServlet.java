package jp.sf.ssoproxy.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.access.AccessManager;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.HostConfig;
import jp.sf.ssoproxy.config.ProxyConfig;
import jp.sf.ssoproxy.helper.ErrorHandlingHelper;
import jp.sf.ssoproxy.helper.LogHelper;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class ProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 778883232389401884L;

    private String errorPage;

    private Locale systemLocale;

    // TODO move s2 component
    private ServletFileUpload servletFileUpload;

    private S2Container container;

    private ErrorHandlingHelper errorHandlingHelper;

    private LogHelper logHelper;

    private ProxyConfig proxyConfig;

    @Override
    public void init() throws ServletException {
        // set an error page
        errorPage = getServletConfig().getInitParameter(
                SSOProxyConstants.ERROR_JSP_KEY);
        if (errorPage == null) {
            errorPage = SSOProxyConstants.DEFAULT_ERROR_JSP;
        }
        // set a system locale
        final String value = getServletConfig().getInitParameter(
                SSOProxyConstants.SYSTEM_LOCALE_KEY);
        if (value != null) {
            try {
                final String[] values = value.split("_");
                if (values.length == 3) {
                    systemLocale = new Locale(values[0], values[1], values[2]);
                } else if (values.length == 2) {
                    systemLocale = new Locale(values[0], values[1]);
                } else if (values.length == 1) {
                    systemLocale = new Locale(values[0]);
                } else {
                    systemLocale = Locale.ENGLISH;
                }
            } catch (final RuntimeException e) {
                systemLocale = Locale.ENGLISH;
            }
        } else {
            systemLocale = Locale.ENGLISH;
        }

        // Create a factory for disk-based file items
        final DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        final String sizeThreshold = getServletConfig().getInitParameter(
                "uploadSizeThreshold");
        if (sizeThreshold != null) {
            factory.setSizeThreshold(Integer.parseInt(sizeThreshold));
        }
        final String repositoryPath = getServletConfig().getInitParameter(
                "uploadRepositoryPath");
        if (repositoryPath != null) {
            factory.setRepository(new File(repositoryPath));
        }

        // Create a new file upload handler
        servletFileUpload = new ServletFileUpload(factory);

        // Components
        container = SingletonS2ContainerFactory.getContainer();
        errorHandlingHelper = SingletonS2Container
                .getComponent("errorHandlingHelper");
        logHelper = SingletonS2Container.getComponent("logHelper");
        proxyConfig = SingletonS2Container.getComponent("proxyConfig");

    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        // needs to check a user name from session by filter

        final String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            // error
            final String erroCode = "000002";
            logHelper.printLog(erroCode, null, systemLocale);
            errorHandlingHelper.forwardErrorPage(request, response, erroCode,
                    null, errorPage);
            return;
        }

        final int actualPathPos = pathInfo.indexOf('/', 1);
        if (actualPathPos < 0) {
            // error
            final String erroCode = "000003";
            logHelper.printLog(erroCode, null, systemLocale);
            errorHandlingHelper.forwardErrorPage(request, response, erroCode,
                    null, errorPage);
            return;
        }

        // Get a host configuration by a host config name of a path info
        final String hostConfigName = pathInfo.substring(1, actualPathPos);
        final HostConfig hostConfig = proxyConfig.getHostConfig(hostConfigName);
        if (hostConfig == null) {
            // error
            final String erroCode = "000004";
            logHelper.printLog(erroCode, new Object[] { hostConfigName },
                    systemLocale);
            errorHandlingHelper.forwardErrorPage(request, response, erroCode,
                    new Object[] { hostConfigName }, errorPage);
            return;
        }

        // Get a access manager by the host config
        final String accessManagerName = hostConfig.getAccessManagerName();
        if (!container.hasComponentDef(accessManagerName)) {
            // error
            final String erroCode = "000005";
            logHelper.printLog(erroCode, new Object[] { accessManagerName,
                    hostConfigName }, systemLocale);
            errorHandlingHelper.forwardErrorPage(request, response, erroCode,
                    new Object[] { accessManagerName, hostConfigName },
                    errorPage);
            return;
        }
        final AccessManager accessManager = SingletonS2Container
                .getComponent(accessManagerName);

        try {
            // set request encoding
            request.setCharacterEncoding(hostConfig.getEncoding());
        } catch (final UnsupportedEncodingException e) {
            // error
            logHelper.printLog(e, systemLocale);
            errorHandlingHelper.forwardErrorPage(request, response, e,
                    errorPage);
            return;
        }

        // Check that we have a file upload request
        final boolean isMultipart = ServletFileUpload
                .isMultipartContent(request);

        Map<String, Object> resultMap = null;
        try {
            final String url = hostConfig.buildUrl(pathInfo
                    .substring(actualPathPos));
            if (isMultipart) {
                // TODO?
            } else {
                // TODO the url has request parameters or not when checking it in a following method...
                final AuthConfig authConfig = hostConfig.getAuthConfig(
                        request.getMethod(), url, request.getParameterMap());

                //  check a login page
                if (authConfig != null) {
                    // Check a login page
                    resultMap = accessManager.sendLoginPageRequest(request,
                            hostConfig, authConfig);
                    final int statusCode = ((Integer) resultMap
                            .get(SSOProxyConstants.STATUS_CODE_PARAM))
                            .intValue();
                    if (statusCode == 200
                            && authConfig
                                    .checkLoginPage((InputStream) resultMap
                                            .get(SSOProxyConstants.LOGIN_BODY_INPUT_STREAM_PARAM))) {
                        // release result
                        accessManager.release(resultMap);
                        // Send auth info
                        resultMap = accessManager.sendAuthPageRequest(request,
                                hostConfig, authConfig);
                    }
                } else {
                    // get a content from a back-end server
                    resultMap = accessManager.sendRequest(request, hostConfig,
                            url, hostConfig.getEncoding());
                }
            }

            final int statusCode = ((Integer) resultMap
                    .get(SSOProxyConstants.STATUS_CODE_PARAM)).intValue();
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
                                    .get(SSOProxyConstants.MIME_TYPE_PARAM)));
            // flush
            response.flushBuffer();
        } catch (final Exception e) {
            // error
            logHelper.printLog(e, systemLocale);
            //TODO error
            errorHandlingHelper.forwardErrorPage(request, response, e,
                    errorPage);
            return;
        } finally {
            accessManager.release(resultMap);
        }

    }

    @Override
    protected void doPost(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        doGet(request, response);
    }

    private boolean isRedirectStatusCode(final int statusCode) {
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
