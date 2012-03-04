package jp.sf.ssoproxy.config.impl;

import java.util.HashMap;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.ConfigException;
import jp.sf.ssoproxy.config.HostConfig;

import org.apache.commons.httpclient.cookie.CookiePolicy;

public class HostConfigImpl implements HostConfig {
    private static final String HTTP_SCHEME = "http";

    private static final String DEFAULT_FORWARDER = "defaultForwarder";

    private static final String UTF_8 = "UTF-8";

    private static final String DEFAULT_ACCESS_MANAGER = "defaultAccessManager";

    private static final String PORT_SEPARATOR = ":";

    private static final String SCHEME_SEPARATOR = "://";

    private String name;

    private String scheme;

    private String host;

    private String port;

    private String encoding;

    private String accessManagerName;

    private Map<String, String> forwarderMap;

    private AuthConfig[] authConfigs;

    private boolean singleCookieHeader;

    private String cookiePolicy;

    private int connectionTimeout;

    public HostConfigImpl() {
        scheme = HTTP_SCHEME;
        encoding = UTF_8;
        accessManagerName = DEFAULT_ACCESS_MANAGER;
        forwarderMap = new HashMap<String, String>();
        cookiePolicy = SSOProxyConstants.STANDARD_BROWSER;
        singleCookieHeader = false;
        connectionTimeout = 30000;
    }

    public AuthConfig getAuthConfig(String method, String url,
            Map<String, String[]> params) throws ConfigException {
        if (authConfigs != null) {
            for (int i = 0; i < authConfigs.length; i++) {
                if (authConfigs[i].checkLoginPageUrl(method, url, params)) {
                    return authConfigs[i];
                }
            }
        }
        return null;
    }

    public String buildUrl(String path) {
        StringBuilder buf = new StringBuilder();
        buf.append(scheme).append(SCHEME_SEPARATOR).append(host);
        if (port != null) {
            buf.append(PORT_SEPARATOR).append(port);
        }
        buf.append(path);
        return buf.toString();
    }

    public String getForwarderName(String mimeType) {
        String forwarderName = forwarderMap.get(mimeType);
        if (forwarderName != null) {
            return forwarderName;
        }
        return DEFAULT_FORWARDER;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getAccessManagerName() {
        return accessManagerName;
    }

    public void setAccessManagerName(String accessManagerName) {
        this.accessManagerName = accessManagerName;
    }

    public Map<String, String> getForwarderMap() {
        return forwarderMap;
    }

    public void setForwarderMap(Map<String, String> forwarderMap) {
        this.forwarderMap = forwarderMap;
    }

    public AuthConfig[] getAuthConfigs() {
        return authConfigs;
    }

    public void setAuthConfigs(AuthConfig[] authConfigs) {
        this.authConfigs = authConfigs;
    }

    public boolean isSingleCookieHeader() {
        return singleCookieHeader;
    }

    public void setSingleCookieHeader(boolean singleCookieHeader) {
        this.singleCookieHeader = singleCookieHeader;
    }

    public String getCookiePolicy() {
        return cookiePolicy;
    }

    public void setCookiePolicy(String cookiePolicy) {
        this.cookiePolicy = cookiePolicy;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

}
