package jp.sf.ssoproxy;

public class SSOProxyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected String messageId;

    protected Object[] args;

    public SSOProxyException(final String messageId) {
        super(messageId);
        this.messageId = messageId;
    }

    public SSOProxyException(final String messageId, final Object[] args) {
        super(messageId);
        this.messageId = messageId;
        this.args = args;
    }

    public SSOProxyException(final String messageId, final String message,
            final Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
    }

    public SSOProxyException(final String messageId, final Object[] args,
            final String message, final Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
        this.args = args;
    }

    public SSOProxyException(final String messageId, final String message) {
        super(message);
        this.messageId = messageId;
    }

    public SSOProxyException(final String messageId, final Object[] args,
            final String message) {
        super(message);
        this.messageId = messageId;
        this.args = args;
    }

    public SSOProxyException(final String messageId, final Throwable cause) {
        super(cause);
        this.messageId = messageId;
    }

    public SSOProxyException(final String messageId, final Object[] args,
            final Throwable cause) {
        super(cause);
        this.messageId = messageId;
        this.args = args;
    }

    /**
     * @return Returns the messageId.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId
     *            The messageId to set.
     */
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return Returns the args.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @param args
     *            The args to set.
     */
    public void setArgs(final Object[] args) {
        this.args = args;
    }

}