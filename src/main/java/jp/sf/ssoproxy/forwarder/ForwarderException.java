package jp.sf.ssoproxy.forwarder;

import jp.sf.ssoproxy.SSOProxyException;

public class ForwarderException extends SSOProxyException {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    public ForwarderException(final String messageId) {
        super(messageId);
    }

    public ForwarderException(final String messageId, final Object[] args) {
        super(messageId, args);
    }

    public ForwarderException(final String messageId, final String message,
            final Throwable cause) {
        super(messageId, message, cause);
    }

    public ForwarderException(final String messageId, final Object[] args,
            final String message, final Throwable cause) {
        super(messageId, args, message, cause);
    }

    public ForwarderException(final String messageId, final String message) {
        super(messageId, message);
    }

    public ForwarderException(final String messageId, final Object[] args,
            final String message) {
        super(messageId, args, message);
    }

    public ForwarderException(final String messageId, final Throwable cause) {
        super(messageId, cause);
    }

    public ForwarderException(final String messageId, final Object[] args,
            final Throwable cause) {
        super(messageId, args, cause);
    }
}
