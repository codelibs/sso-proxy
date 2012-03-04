package jp.sf.ssoproxy.config;

import jp.sf.ssoproxy.SSOProxyException;

public class ConfigException extends SSOProxyException {

    private static final long serialVersionUID = 1L;

    public ConfigException(final String messageId) {
        super(messageId);
    }

    public ConfigException(final String messageId, final Object[] args) {
        super(messageId, args);
    }

    public ConfigException(final String messageId, final String message,
            final Throwable cause) {
        super(messageId, message, cause);
    }

    public ConfigException(final String messageId, final Object[] args,
            final String message, final Throwable cause) {
        super(messageId, args, message, cause);
    }

    public ConfigException(final String messageId, final String message) {
        super(messageId, message);
    }

    public ConfigException(final String messageId, final Object[] args,
            final String message) {
        super(messageId, args, message);
    }

    public ConfigException(final String messageId, final Throwable cause) {
        super(messageId, cause);
    }

    public ConfigException(final String messageId, final Object[] args,
            final Throwable cause) {
        super(messageId, args, cause);
    }
}
