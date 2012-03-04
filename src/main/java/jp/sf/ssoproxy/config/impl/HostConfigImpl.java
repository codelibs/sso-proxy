package jp.sf.ssoproxy.config.impl;

import java.util.HashMap;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.HostConfig;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.seasar.framework.container.annotation.tiger.InitMethod;

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

    private HttpClient httpClient;

    private boolean singleCookieHeader = false;

    private String cookiePolicy = SSOProxyConstants.STANDARD_BROWSER;

    private int connectionTimeout = 30000;

    private int maxTotalConnections = 20;

    private boolean staleCheckingEnabled = true;

    private int soTimeout = 0;

    private int linger = -1;

    private String proxyHost;

    private int proxyPort = 0;

    private Credentials proxyCredentials;

    public HostConfigImpl() {
        scheme = HTTP_SCHEME;
        encoding = UTF_8;
        accessManagerName = DEFAULT_ACCESS_MANAGER;
        forwarderMap = new HashMap<String, String>();
    }

    @InitMethod
    public void init() {

        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(connectionTimeout);
        params.setMaxTotalConnections(maxTotalConnections);
        params.setStaleCheckingEnabled(staleCheckingEnabled);
        params.setSoTimeout(soTimeout);
        params.setLinger(linger);
        connectionManager.setParams(params);

        httpClient = new org.apache.commons.httpclient.HttpClient(
                connectionManager);

        // proxy
        if (proxyHost != null && proxyPort > 0) {
            httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
            if (proxyCredentials != null) {
                httpClient.getState().setProxyCredentials(
                        new AuthScope(proxyHost, proxyPort), proxyCredentials);
            }
        }

        // Cookie Policy
        httpClient.getParams().setCookiePolicy(getCookiePolicy());
        // Single Cookie Header
        if (isSingleCookieHeader()) {
            httpClient.getParams().setBooleanParameter(
                    HttpMethodParams.SINGLE_COOKIE_HEADER, true);
        }
        // httpclient.getParams().setVersion(HttpVersion.HTTP_1_0); // HTTP 1.1 returns a content encoded by gzip.
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public AuthConfig getAuthConfig(final String method, final String url,
            final Map<String, String[]> params) {
        if (authConfigs != null) {
            for (final AuthConfig authConfig : authConfigs) {
                if (authConfig.checkLoginPageUrl(method, url, params)) {
                    return authConfig;
                }
            }
        }
        return null;
    }

    @Override
    public String buildUrl(final String path) {
        final StringBuilder buf = new StringBuilder();
        buf.append(scheme).append(SCHEME_SEPARATOR).append(host);
        if (port != null) {
            buf.append(PORT_SEPARATOR).append(port);
        }
        buf.append(path);
        return buf.toString();
    }

    @Override
    public String getForwarderName(final String mimeType) {
        final String forwarderName = forwarderMap.get(mimeType);
        if (forwarderName != null) {
            return forwarderName;
        }
        return DEFAULT_FORWARDER;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getAccessManagerName() {
        return accessManagerName;
    }

    public void setAccessManagerName(final String accessManagerName) {
        this.accessManagerName = accessManagerName;
    }

    public Map<String, String> getForwarderMap() {
        return forwarderMap;
    }

    public void setForwarderMap(final Map<String, String> forwarderMap) {
        this.forwarderMap = forwarderMap;
    }

    public AuthConfig[] getAuthConfigs() {
        return authConfigs;
    }

    public void setAuthConfigs(final AuthConfig[] authConfigs) {
        this.authConfigs = authConfigs;
    }

    public boolean isSingleCookieHeader() {
        return singleCookieHeader;
    }

    public void setSingleCookieHeader(final boolean singleCookieHeader) {
        this.singleCookieHeader = singleCookieHeader;
    }

    public String getCookiePolicy() {
        return cookiePolicy;
    }

    public void setCookiePolicy(final String cookiePolicy) {
        this.cookiePolicy = cookiePolicy;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the maxTotalConnections
     */
    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    /**
     * @param maxTotalConnections the maxTotalConnections to set
     */
    public void setMaxTotalConnections(final int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    /**
     * @return the staleCheckingEnabled
     */
    public boolean isStaleCheckingEnabled() {
        return staleCheckingEnabled;
    }

    /**
     * @param staleCheckingEnabled the staleCheckingEnabled to set
     */
    public void setStaleCheckingEnabled(final boolean staleCheckingEnabled) {
        this.staleCheckingEnabled = staleCheckingEnabled;
    }

    /**
     * @return the soTimeout
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * @param soTimeout the soTimeout to set
     */
    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * @return the linger
     */
    public int getLinger() {
        return linger;
    }

    /**
     * @param linger the linger to set
     */
    public void setLinger(final int linger) {
        this.linger = linger;
    }

    /**
     * @return the proxyHost
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost the proxyHost to set
     */
    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return the proxyPort
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return the proxyCredentials
     */
    public Credentials getProxyCredentials() {
        return proxyCredentials;
    }

    /**
     * @param proxyCredentials the proxyCredentials to set
     */
    public void setProxyCredentials(final Credentials proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }

}
