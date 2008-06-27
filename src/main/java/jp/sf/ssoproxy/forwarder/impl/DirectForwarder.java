package jp.sf.ssoproxy.forwarder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.SSOProxyException;
import jp.sf.ssoproxy.forwarder.Forwarder;
import jp.sf.ssoproxy.forwarder.ForwarderException;

public class DirectForwarder implements Forwarder {
    private static final int BLOCK_SIZE = 4096;

    public void forward(Map<String, Object> props, InputStream is,
            OutputStream os) throws SSOProxyException {
        try {
            byte[] bytes = new byte[BLOCK_SIZE];
            try {
                int length = is.read(bytes);
                while (length != -1) {
                    if (length != 0) {
                        os.write(bytes, 0, length);
                    }
                    length = is.read(bytes);
                }
            } finally {
                bytes = null;
            }
        } catch (IOException e) {
            // error
            throw new ForwarderException("000007", new Object[] { props
                    .get(SSOProxyConstraints.URL_PARAM) }, e);
        }
    }

}
