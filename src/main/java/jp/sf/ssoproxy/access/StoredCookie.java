package jp.sf.ssoproxy.access;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.httpclient.Cookie;

public class StoredCookie implements Serializable {

    private static final long serialVersionUID = -4076144473672176922L;

    private String domain;

    private String name;

    private String value;

    private String path;

    private Date expiryDate;

    private boolean secure;

    public StoredCookie(Cookie httpClientCookie) {
        this(httpClientCookie.getDomain(), httpClientCookie.getName(),
                httpClientCookie.getValue(), httpClientCookie.getPath(),
                httpClientCookie.getExpiryDate(), httpClientCookie.getSecure());
    }

    public StoredCookie(String domain, String name, String value, String path,
            Date expiryDate, boolean secure) {
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
