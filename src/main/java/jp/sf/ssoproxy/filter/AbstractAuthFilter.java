package jp.sf.ssoproxy.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.helper.ErrorHandlingHelper;
import jp.sf.ssoproxy.helper.LogHelper;

import org.seasar.framework.container.SingletonS2Container;

public abstract class AbstractAuthFilter implements Filter {

    protected String errorPage;

    protected Locale systemLocale;

    protected String currentUserKey;

    protected ErrorHandlingHelper errorHandlingHelper;

    protected LogHelper logHelper;

    @Override
    public void init(final FilterConfig config) throws ServletException {
        // set an current user key
        currentUserKey = config
                .getInitParameter(SSOProxyConstants.CURRENT_REMOTE_USER_KEY);
        if (currentUserKey == null) {
            currentUserKey = SSOProxyConstants.CURRENT_REMOTE_USER;
        }

        // set an error page
        errorPage = config.getInitParameter(SSOProxyConstants.ERROR_JSP_KEY);
        if (errorPage == null) {
            errorPage = SSOProxyConstants.DEFAULT_ERROR_JSP;
        }

        // set a system locale
        final String value = config
                .getInitParameter(SSOProxyConstants.SYSTEM_LOCALE_KEY);
        if (value != null) {
            try {
                final String[] values = value.split("_");
                if (values.length == 3) {
                    systemLocale = new Locale(values[0], values[1], values[2]);
                } else if (values.length == 2) {
                    systemLocale = new Locale(values[0], values[1]);
                } else if (values.length == 1) {
                    systemLocale = new Locale(values[0]);
                } else {
                    systemLocale = Locale.ENGLISH;
                }
            } catch (final RuntimeException e) {
                systemLocale = Locale.ENGLISH;
            }
        } else {
            systemLocale = Locale.ENGLISH;
        }

        errorHandlingHelper = SingletonS2Container
                .getComponent("errorHandlingHelper");
        logHelper = SingletonS2Container.getComponent("logHelper");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(final ServletRequest request,
            final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest
                && response instanceof HttpServletResponse) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final String remoteUser = getRemoteUser(httpRequest);
            if (remoteUser != null) {
                chain.doFilter(request, response);
            } else {
                // error
                final String erroCode = "000008";
                logHelper.printLog(erroCode, null, systemLocale);
                errorHandlingHelper.forwardErrorPage(
                        (HttpServletRequest) request,
                        (HttpServletResponse) response, erroCode, null,
                        errorPage);
            }
        } else {
            //TODO redirect?
            response.getWriter().write("Not supported.");
        }
    }

    public abstract String getRemoteUser(HttpServletRequest request);
}