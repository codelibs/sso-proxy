package jp.sf.ssoproxy.forwarder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface Forwarder {
    void forward(Map<String, Object> props, InputStream is, OutputStream os);

}