package jp.sf.ssoproxy.builder;

import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.config.HostConfig;

import org.apache.commons.httpclient.HttpMethod;

public interface RequestBuilder {
    public static int DEFAULT_PROCESS = 1;

    public static int LOGIN_PROCESS = 1 << 1;

    public static int AUTH_PROCESS = 1 << 2;

    public abstract void build(int type, HttpServletRequest request,
            HostConfig hostConfig, HttpMethod httpMethod,
            RequestBuilderChain chain);

}