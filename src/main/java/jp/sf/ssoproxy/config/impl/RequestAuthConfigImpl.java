package jp.sf.ssoproxy.config.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RequestAuthConfigImpl extends AbstractAuthConfig {
    private String usernameKey;

    private String passwordKey;

    private String usernameAttributeKey;

    private String passwordAttributeKey;

    private RequestType requestType;

    @Override
    protected String getDataValue(final HttpServletRequest request,
            final String value) {
        if (usernameKey.equals(value)) {
            return getValue(request, usernameAttributeKey);
        } else if (passwordKey.equals(value)) {
            return getValue(request, passwordAttributeKey);
        }
        return null;
    }

    private String getValue(final HttpServletRequest request, final String key) {
        switch (requestType) {
        case COOKIE:
            final Cookie[] cookies = request.getCookies();
            for (final Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            break;
        case PARAMETER:
            return request.getParameter(key);
        case HEADER:
        default:
            return request.getHeader(key);
        }
        return null;
    }

    public String getUsernameKey() {
        return usernameKey;
    }

    public void setUsernameKey(final String usernameKey) {
        this.usernameKey = usernameKey;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(final String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getUsernameAttributeKey() {
        return usernameAttributeKey;
    }

    public void setUsernameAttributeKey(final String usernameAttributeKey) {
        this.usernameAttributeKey = usernameAttributeKey;
    }

    public String getPasswordAttributeKey() {
        return passwordAttributeKey;
    }

    public void setPasswordAttributeKey(final String passwordAttributeKey) {
        this.passwordAttributeKey = passwordAttributeKey;
    }

    /**
     * @return the requestType
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * @param requestType the requestType to set
     */
    public void setRequestType(final RequestType requestType) {
        this.requestType = requestType;
    }

    public enum RequestType {
        HEADER, COOKIE, PARAMETER;
    }
}
