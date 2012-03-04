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
import jp.sf.ssoproxy.util.ErrorHandlingUtil;

public abstract class AbstractAuthFilter implements Filter {

    protected String errorPage;

    protected Locale systemLocale;

    protected String currentUserKey;

    public void init(FilterConfig config) throws ServletException {
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
        String value = config
                .getInitParameter(SSOProxyConstants.SYSTEM_LOCALE_KEY);
        if (value != null) {
            try {
                String[] values = value.split("_");
                if (values.length == 3) {
                    systemLocale = new Locale(values[0], values[1], values[2]);
                } else if (values.length == 2) {
                    systemLocale = new Locale(values[0], values[1]);
                } else if (values.length == 1) {
                    systemLocale = new Locale(values[0]);
                } else {
                    systemLocale = Locale.ENGLISH;
                }
            } catch (RuntimeException e) {
                systemLocale = Locale.ENGLISH;
            }
        } else {
            systemLocale = Locale.ENGLISH;
        }
    }

    public void destroy() {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest
                && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String remoteUser = getRemoteUser(httpRequest);
            if (remoteUser != null) {
                chain.doFilter(request, response);
            } else {
                // error
                String erroCode = "000008";
                ErrorHandlingUtil.printLog(erroCode, null, systemLocale);
                ErrorHandlingUtil.forwardErrorPage(
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