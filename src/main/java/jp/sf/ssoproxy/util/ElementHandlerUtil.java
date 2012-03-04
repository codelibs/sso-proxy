package jp.sf.ssoproxy.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.config.ProxyConfig;
import jp.sf.ssoproxy.handler.html.HtmlHandler;

public class ElementHandlerUtil {
    public static String buildUrl(final HtmlHandler htmlHandler, String url) {
        final Map<String, Object> props = htmlHandler.getProperties();
        final String currentUrl = (String) props
                .get(SSOProxyConstants.URL_PARAM);
        if (url.indexOf("://") < 0) {
            try {
                url = new URL(new URL(currentUrl), url).toString();
            } catch (final MalformedURLException e) {
                // TODO
            }
        }
        final ProxyConfig proxyConfig = (ProxyConfig) props
                .get(SSOProxyConstants.PROXY_CONFIG_PARAM);
        return proxyConfig.buildProxyUrl(url);
    }
}
