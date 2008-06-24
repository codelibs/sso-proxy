package jp.sf.ssoproxy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.SSOProxyConstraints;

public class SessionAuthFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {

    }

    public void destroy() {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String remoteUser = (String) httpRequest.getSession().getAttribute(
                    SSOProxyConstraints.CURRENT_REMOTE_USER);
            if (remoteUser != null) {
                chain.doFilter(request, response);
            } else {
                //TODO redirect?
            }
        } else {
            //TODO redirect?

        }
    }

}
