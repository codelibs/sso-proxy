package jp.sf.ssoproxy.config;

import java.util.Map;

public interface HostConfig {
    public abstract String buildUrl(String path);

    public abstract String getName();

    public abstract String getEncoding();

    public abstract String getAccessManagerName();

    public abstract String getForwarderName(String mimeType);

    public abstract AuthConfig getAuthConfig(String method, String url,
            Map<String, String[]> params) throws ConfigException;

    public abstract boolean isSingleCookieHeader();

    public abstract String getCookiePolicy();

    public abstract int getConnectionTimeout();
}