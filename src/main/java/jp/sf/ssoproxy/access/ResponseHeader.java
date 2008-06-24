package jp.sf.ssoproxy.access;

import java.io.Serializable;

import org.apache.commons.httpclient.Header;

public class ResponseHeader implements Serializable {

    private static final long serialVersionUID = 7743538118092327345L;

    private String name;

    private String value;

    public ResponseHeader(Header header) {
        name = header.getName();
        value = header.getValue();
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
}
