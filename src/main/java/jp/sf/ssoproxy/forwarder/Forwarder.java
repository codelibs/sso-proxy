package jp.sf.ssoproxy.forwarder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyException;

public interface Forwarder {

    public abstract void forward(Map<String, Object> props, InputStream is,
            OutputStream os) throws SSOProxyException;

}