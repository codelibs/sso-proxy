package jp.sf.ssoproxy.config;

import jp.sf.ssoproxy.SSOProxyException;

public class ConfigException extends SSOProxyException {

    private static final long serialVersionUID = 7749580473857490597L;

    public ConfigException(String messageId) {
        super(messageId);
    }

    public ConfigException(String messageId, Object[] args) {
        super(messageId, args);
    }

    public ConfigException(String messageId, String message, Throwable cause) {
        super(messageId, message, cause);
    }

    public ConfigException(String messageId, Object[] args, String message,
            Throwable cause) {
        super(messageId, args, message, cause);
    }

    public ConfigException(String messageId, String message) {
        super(messageId, message);
    }

    public ConfigException(String messageId, Object[] args, String message) {
        super(messageId, args, message);
    }

    public ConfigException(String messageId, Throwable cause) {
        super(messageId, cause);
    }

    public ConfigException(String messageId, Object[] args, Throwable cause) {
        super(messageId, args, cause);
    }
}
