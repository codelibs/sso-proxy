package jp.sf.ssoproxy.access;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.HostConfig;
import jp.sf.ssoproxy.config.ProxyConfig;

public interface AccessManager {

    Map<String, Object> sendRequest(HttpServletRequest request,
            HostConfig hostConfig, String url, String encoding);

    void sendResponse(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> resultMap, ProxyConfig proxyConfig,
            String hostConfigName, String forwarderName);

    Map<String, Object> sendLoginPageRequest(HttpServletRequest request,
            HostConfig hostConfig, AuthConfig authConfig);

    Map<String, Object> sendAuthPageRequest(HttpServletRequest request,
            HostConfig hostConfig, AuthConfig authConfig);

    void redirectResponse(HttpServletRequest request,
            HttpServletResponse response, Map<String, Object> resultMap,
            ProxyConfig proxyConfig);

    void release(Map<String, Object> resultMap);

}