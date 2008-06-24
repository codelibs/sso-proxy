package jp.sf.ssoproxy.access;

import jp.sf.ssoproxy.SSOProxyException;

public class AccessException extends SSOProxyException {

    public AccessException(String messageId) {
        super(messageId);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, Object[] args) {
        super(messageId, args);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, String message, Throwable cause) {
        super(messageId, message, cause);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, Object[] args, String message,
            Throwable cause) {
        super(messageId, args, message, cause);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, String message) {
        super(messageId, message);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, Object[] args, String message) {
        super(messageId, args, message);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, Throwable cause) {
        super(messageId, cause);
        // TODO Auto-generated constructor stub
    }

    public AccessException(String messageId, Object[] args, Throwable cause) {
        super(messageId, args, cause);
        // TODO Auto-generated constructor stub
    }

}
