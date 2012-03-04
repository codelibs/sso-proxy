package jp.sf.ssoproxy.access;

import java.io.Serializable;

import org.apache.commons.httpclient.Header;

public class ResponseHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String value;

    public ResponseHeader(final Header header) {
        name = header.getName();
        value = header.getValue();
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
}
