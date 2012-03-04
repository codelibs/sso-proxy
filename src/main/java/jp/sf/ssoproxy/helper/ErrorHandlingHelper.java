package jp.sf.ssoproxy.helper;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.SSOProxyException;

public class ErrorHandlingHelper {

    @Resource
    protected MessageHelper messageHelper;

    public void forwardErrorPage(final HttpServletRequest request,
            final HttpServletResponse response, final Throwable t,
            final String errorPage) throws ServletException, IOException {
        String errorCode = null;
        if (t instanceof SSOProxyException) {
            final SSOProxyException spe = (SSOProxyException) t;
            errorCode = spe.getMessageId();
            forwardErrorPage(request, response, errorCode, spe.getArgs(),
                    errorPage);
        } else {
            forwardErrorPage(request, response, "000001", null, errorPage);
        }
    }

    public void forwardErrorPage(final HttpServletRequest request,
            final HttpServletResponse response, final String errorCode,
            final Object[] args, final String errorPage)
            throws ServletException, IOException {
        final String errorMessage = messageHelper.getMessage(errorCode, args,
                request.getLocale());

        // TODO response status code
        response.setStatus(500);

        request.setAttribute(SSOProxyConstants.ERROR_CODE, errorCode);
        request.setAttribute(SSOProxyConstants.ERROR_MESSAGE, errorMessage);

        final RequestDispatcher rd = request.getRequestDispatcher(errorPage);
        rd.forward(request, response);
    }

}
