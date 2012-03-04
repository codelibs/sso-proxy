package jp.sf.ssoproxy.config;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

public interface HostConfig {
    String buildUrl(String path);

    String getName();

    String getEncoding();

    String getAccessManagerName();

    String getForwarderName(String mimeType);

    AuthConfig getAuthConfig(String method, String url,
            Map<String, String[]> params);

    HttpClient getHttpClient();
}