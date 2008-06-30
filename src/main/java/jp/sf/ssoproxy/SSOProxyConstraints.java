package jp.sf.ssoproxy;

public class SSOProxyConstraints {
    public static final String PREFIX = "ssoproxy.";

    public static final String STORED_COOKIE_LIST = PREFIX
            + "StoredCookieList_";

    public static final String MIME_TYPE_PARAM = PREFIX + "MimeType";

    public static final String RESPONSE_HEADERS_PARAM = PREFIX
            + "ResponseHeaders";

    public static final String RESPONSE_BODY_FILE_PARAM = PREFIX
            + "ResponseBodyFile";

    public static final String RESPONSE_BODY_INPUT_STREAM_PARAM = PREFIX
            + "ResponseBodyInputStream";

    public static final String LOGIN_BODY_FILE_PARAM = PREFIX + "LoginBodyFile";

    public static final String LOGIN_BODY_INPUT_STREAM_PARAM = PREFIX
            + "LoginBodyInputStream";

    public static final String STATUS_CODE_PARAM = PREFIX + "StatusCode";

    public static final String INPUT_ENCODING_PARAM = PREFIX + "InputEncoding";

    public static final String URL_PARAM = PREFIX + "Url";

    public static final String ORIGINAL_URL_PARAM = PREFIX + "OriginalUrl";

    public static final String OUTPUT_ENCODING_PARAM = PREFIX
            + "OutputEncoding";

    public static final String PROXY_CONFIG_PARAM = PREFIX + "ProxyConfig";

    public static final String POST_METHOD = "POST";

    public static final String GET_METHOD = "GET";

    public static final String REQUEST_PARAM_QUERY_SEPARATOR = "&";

    public static final String REQUEST_PARAM_URL_SEPARATOR = "?";

    public static final String REQUEST_PARAM_EQUAL = "=";

    public static final String CURRENT_REMOTE_USER = "CurrentRemoteUser";

    public static final String ERROR_CODE = "ErrorCode";

    public static final String ERROR_MESSAGE = "ErrorMessage";

    public static final String SYSTEM_LOCALE_KEY = "sytemLocale";

    public static final String ERROR_JSP_KEY = "errorPage";

    public static final String DEFAULT_ERROR_JSP = "/error.jsp";

    public static final String CURRENT_REMOTE_USER_KEY = "currentUserKey";

}
