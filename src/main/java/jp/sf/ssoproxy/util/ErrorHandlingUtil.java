package jp.sf.ssoproxy.util;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.SSOProxyException;
import jp.sf.ssoproxy.servlet.ProxyServlet;

public class ErrorHandlingUtil {

    /**
     * Logger for this class
     */
    private static final Log log = LogFactory.getLog(ProxyServlet.class);

    public static void forwardErrorPage(HttpServletRequest request,
            HttpServletResponse response, Throwable t, String errorPage)
            throws ServletException, IOException {
        String errorCode = null;
        if (t instanceof SSOProxyException) {
            SSOProxyException spe = (SSOProxyException) t;
            errorCode = spe.getMessageId();
            forwardErrorPage(request, response, errorCode, spe.getArgs(),
                    errorPage);
        } else {
            forwardErrorPage(request, response, "000001", null, errorPage);
        }
    }

    public static void forwardErrorPage(HttpServletRequest request,
            HttpServletResponse response, String errorCode, Object[] args,
            String errorPage) throws ServletException, IOException {
        String errorMessage = MessageUtil.getMessage(errorCode, args, request
                .getLocale());

        // TODO response status code
        response.setStatus(500);

        request.setAttribute(SSOProxyConstraints.ERROR_CODE, errorCode);
        request.setAttribute(SSOProxyConstraints.ERROR_MESSAGE, errorMessage);

        RequestDispatcher rd = request.getRequestDispatcher(errorPage);
        rd.forward(request, response);
    }

    public static void printLog(Throwable t, Locale systemLocale) {
        String errorCode = null;
        String errorMessage = null;
        if (t instanceof SSOProxyException) {
            SSOProxyException spe = (SSOProxyException) t;
            errorCode = spe.getMessageId();
            errorMessage = MessageUtil.getMessage(errorCode, spe.getArgs(),
                    systemLocale);
        } else {
            errorCode = "000001";
            errorMessage = MessageUtil.getMessage(errorCode, systemLocale);
        }

        // TODO logging level
        log.error(errorMessage, t);
    }

    public static void printLog(String errorCode, Object[] args,
            Locale systemLocale) {
        String errorMessage = MessageUtil.getMessage(errorCode, args,
                systemLocale);

        // TODO logging level
        log.error(errorMessage);
    }
}
