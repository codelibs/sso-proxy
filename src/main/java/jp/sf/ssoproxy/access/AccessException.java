package jp.sf.ssoproxy.access;

import jp.sf.ssoproxy.SSOProxyException;

public class AccessException extends SSOProxyException {

    private static final long serialVersionUID = 1L;

    public AccessException(final String messageId) {
        super(messageId);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final Object[] args) {
        super(messageId, args);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final String message,
            final Throwable cause) {
        super(messageId, message, cause);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final Object[] args,
            final String message, final Throwable cause) {
        super(messageId, args, message, cause);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final String message) {
        super(messageId, message);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final Object[] args,
            final String message) {
        super(messageId, args, message);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final Throwable cause) {
        super(messageId, cause);
        // TODO Auto-generated constructor stub
    }

    public AccessException(final String messageId, final Object[] args,
            final Throwable cause) {
        super(messageId, args, cause);
        // TODO Auto-generated constructor stub
    }

}
