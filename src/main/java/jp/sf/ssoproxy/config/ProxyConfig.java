package jp.sf.ssoproxy.config;

public interface ProxyConfig {

    public abstract HostConfig getHostConfig(String hostConfigName);

    public abstract String buildProxyUrl(String url);

    public abstract String getRootPath();

    public abstract void setRootPath(String rootPath);
}