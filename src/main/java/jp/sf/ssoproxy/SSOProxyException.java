package jp.sf.ssoproxy;

public class SSOProxyException extends Exception {

    private static final long serialVersionUID = -8536748731584194468L;

    protected String messageId;

    protected Object[] args;

    public SSOProxyException(String messageId) {
        super(messageId);
        this.messageId = messageId;
    }

    public SSOProxyException(String messageId, Object[] args) {
        super(messageId);
        this.messageId = messageId;
        this.args = args;
    }

    public SSOProxyException(String messageId, String message, Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
    }

    public SSOProxyException(String messageId, Object[] args, String message,
            Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
        this.args = args;
    }

    public SSOProxyException(String messageId, String message) {
        super(message);
        this.messageId = messageId;
    }

    public SSOProxyException(String messageId, Object[] args, String message) {
        super(message);
        this.messageId = messageId;
        this.args = args;
    }

    public SSOProxyException(String messageId, Throwable cause) {
        super(cause);
        this.messageId = messageId;
    }

    public SSOProxyException(String messageId, Object[] args, Throwable cause) {
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
    public void setMessageId(String messageId) {
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
    public void setArgs(Object[] args) {
        this.args = args;
    }

}