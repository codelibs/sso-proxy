package jp.sf.ssoproxy.config;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;

public interface AuthConfig {

    boolean checkLoginPageUrl(String method, String url,
            Map<String, String[]> params);

    boolean checkLoginPage(InputStream inputStream);

    HttpMethod buildLoginHttpMethod(HttpServletRequest request);

    HttpMethod buildAuthHttpMethod(HttpServletRequest request);

}