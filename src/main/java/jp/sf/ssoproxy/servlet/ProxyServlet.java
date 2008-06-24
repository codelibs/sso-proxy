package jp.sf.ssoproxy.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.access.AccessException;
import jp.sf.ssoproxy.access.AccessManager;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.HostConfig;
import jp.sf.ssoproxy.config.ProxyConfig;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class ProxyServlet extends HttpServlet {

    private static final String PROXY_CONFIG_COMPONENT = "proxyConfig";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        //TODO check a user name from session

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            // TODO
            return;
        }

        int actualPathPos = pathInfo.indexOf("/", 1);
        if (actualPathPos < 0) {
            //TODO
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
            //TODO 
            return;
        }

        // Get a access manager by the host config
        AccessManager accessManager = (AccessManager) container
                .getComponent(hostConfig.getAccessManagerName());
        if (accessManager == null) {
            //TODO 
            return;
        }

        try {
            // set request encoding
            request.setCharacterEncoding(hostConfig.getEncoding());
        } catch (UnsupportedEncodingException e) {
            //TODO 
            e.printStackTrace();
            return;
        }

        Map<String, Object> resultMap = null;
        String url = hostConfig.buildUrl(pathInfo.substring(actualPathPos));
        // TODO the url has request parameters or not when checking it in a following method...
        AuthConfig authConfig = hostConfig.getAuthConfig(request.getMethod(),
                url, request.getParameterMap());

        try {
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

            // store a content to a client
            accessManager.sendResponse(request, response, resultMap,
                    proxyConfig, hostConfigName, hostConfig
                            .getForwarderName((String) resultMap
                                    .get(SSOProxyConstraints.MIME_TYPE_PARAM)));
            // flush
            response.flushBuffer();
        } catch (AccessException e) {
            //TODO 
            e.printStackTrace();
            return;
        } catch (Exception e) {
            //TODO 
            e.printStackTrace();
            return;
        } finally {
            accessManager.release(resultMap);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doPost(req, resp);
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
