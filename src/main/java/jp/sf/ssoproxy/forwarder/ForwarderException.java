package jp.sf.ssoproxy.forwarder;

import jp.sf.ssoproxy.SSOProxyException;

public class ForwarderException extends SSOProxyException {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 5059420965345442529L;

    public ForwarderException(String messageId) {
        super(messageId);
    }

    public ForwarderException(String messageId, Object[] args) {
        super(messageId, args);
    }

    public ForwarderException(String messageId, String message, Throwable cause) {
        super(messageId, message, cause);
    }

    public ForwarderException(String messageId, Object[] args, String message,
            Throwable cause) {
        super(messageId, args, message, cause);
    }

    public ForwarderException(String messageId, String message) {
        super(messageId, message);
    }

    public ForwarderException(String messageId, Object[] args, String message) {
        super(messageId, args, message);
    }

    public ForwarderException(String messageId, Throwable cause) {
        super(messageId, cause);
    }

    public ForwarderException(String messageId, Object[] args, Throwable cause) {
        super(messageId, args, cause);
    }
}
