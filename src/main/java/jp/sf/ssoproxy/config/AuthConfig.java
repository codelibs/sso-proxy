package jp.sf.ssoproxy.config;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;

public interface AuthConfig {

    public abstract boolean checkLoginPageUrl(String method, String url,
            Map<String, String[]> params) throws ConfigException;

    public abstract boolean checkLoginPage(InputStream inputStream)
            throws ConfigException;

    public abstract HttpMethod buildLoginHttpMethod(HttpServletRequest request)
            throws ConfigException;

    public abstract HttpMethod buildAuthHttpMethod(HttpServletRequest request)
            throws ConfigException;

}