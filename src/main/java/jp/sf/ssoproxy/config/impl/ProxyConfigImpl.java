package jp.sf.ssoproxy.config.impl;

import java.util.HashMap;
import java.util.Map;

import jp.sf.ssoproxy.config.HostConfig;
import jp.sf.ssoproxy.config.ProxyConfig;

public class ProxyConfigImpl implements ProxyConfig {
    private static final String PORT_SEPARATOR = ":";

    private static final String ROOT_PATH_SEPARATOR = "/";

    private Map<String, HostConfig> hostConfigMap;

    private Map<String, String> hostPathMap;

    private String rootPath;

    public ProxyConfigImpl(HostConfig[] hostConfigs) {
        hostConfigMap = new HashMap<String, HostConfig>();
        hostPathMap = new HashMap<String, String>();
        for (int i = 0; i < hostConfigs.length; i++) {
            hostConfigMap.put(hostConfigs[i].getName(), hostConfigs[i]);
            hostPathMap.put(hostConfigs[i].getName(), hostConfigs[i]
                    .buildUrl(""));
        }
    }

    public ProxyConfigImpl(Map<String, HostConfig> hostConfigMap,
            Map<String, String> hostPathMap) {
        this.hostConfigMap = hostConfigMap;
        this.hostPathMap = hostPathMap;
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.config.impl.ProxyConfig#getHostConfig(java.lang.String)
     */
    public HostConfig getHostConfig(String hostConfigName) {
        return hostConfigMap.get(hostConfigName);
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String buildProxyUrl(String url) {
        for (Map.Entry<String, String> entry : hostPathMap.entrySet()) {
            if (url.startsWith(entry.getValue())) {
                String path = url.substring(entry.getValue().length());
                // if hostConfig does not have a port and url has a port number(ex. 80)
                if (path.startsWith(PORT_SEPARATOR)) {
                    int index = path.indexOf("/");
                    path = path.substring(index);
                }

                StringBuilder buf = new StringBuilder(rootPath);
                buf.append(ROOT_PATH_SEPARATOR);
                buf.append(entry.getKey());
                buf.append(path);
                return buf.toString();
            }
        }
        return url;
    }
}
