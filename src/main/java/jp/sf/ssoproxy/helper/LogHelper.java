package jp.sf.ssoproxy.helper;

import java.util.Locale;

import javax.annotation.Resource;

import jp.sf.ssoproxy.SSOProxyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {
    /**
     * Logger for this class
     */
    protected Logger logger = LoggerFactory.getLogger("SSOProxy");

    @Resource
    protected MessageHelper messageHelper;

    public void printLog(final Throwable t, final Locale systemLocale) {
        String errorCode = null;
        String errorMessage = null;
        if (t instanceof SSOProxyException) {
            final SSOProxyException spe = (SSOProxyException) t;
            errorCode = spe.getMessageId();
            errorMessage = messageHelper.getMessage(errorCode, spe.getArgs(),
                    systemLocale);
        } else {
            errorCode = "000001";
            errorMessage = messageHelper.getMessage(errorCode, systemLocale);
        }

        // TODO logging level
        logger.error(errorMessage, t);
    }

    public void printLog(final String errorCode, final Object[] args,
            final Locale systemLocale) {
        final String errorMessage = messageHelper.getMessage(errorCode, args,
                systemLocale);

        // TODO logging level
        logger.error(errorMessage);
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(final Logger logger) {
        this.logger = logger;
    }
}
