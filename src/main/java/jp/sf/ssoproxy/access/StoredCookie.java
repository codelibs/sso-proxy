package jp.sf.ssoproxy.access;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.httpclient.Cookie;

public class StoredCookie implements Serializable {

    private static final long serialVersionUID = 1L;

    private String domain;

    private String name;

    private String value;

    private String path;

    private Date expiryDate;

    private boolean secure;

    public StoredCookie(final Cookie httpClientCookie) {
        this(httpClientCookie.getDomain(), httpClientCookie.getName(),
                httpClientCookie.getValue(), httpClientCookie.getPath(),
                httpClientCookie.getExpiryDate(), httpClientCookie.getSecure());
    }

    public StoredCookie(final String domain, final String name,
            final String value, final String path, final Date expiryDate,
            final boolean secure) {
        this.domain = domain;
        this.name = name;
        this.value = value;
        this.path = path;
        this.expiryDate = expiryDate;
        this.secure = secure;
    }

    public Cookie getHttpClientCookie() {
        return new Cookie(domain, name, value, path, expiryDate, secure);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
