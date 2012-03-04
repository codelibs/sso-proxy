package jp.sf.ssoproxy.config;

public interface ProxyConfig {

    HostConfig getHostConfig(String hostConfigName);

    String buildProxyUrl(String url);

    String getRootPath();

    void setRootPath(String rootPath);
}