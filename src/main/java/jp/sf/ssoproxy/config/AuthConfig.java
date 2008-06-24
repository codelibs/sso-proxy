package jp.sf.ssoproxy.config;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.access.AccessException;

import org.apache.commons.httpclient.HttpMethod;

public interface AuthConfig {

    public abstract boolean checkLoginPageUrl(String method, String url,
            Map<String, String[]> params);

    public abstract boolean checkLoginPage(InputStream inputStream);

    public abstract HttpMethod buildLoginHttpMethod(HttpServletRequest request)
            throws AccessException;

    public abstract HttpMethod buildAuthHttpMethod(HttpServletRequest request)
            throws AccessException;

}