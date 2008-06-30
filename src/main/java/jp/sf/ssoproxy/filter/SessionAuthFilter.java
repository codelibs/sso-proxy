package jp.sf.ssoproxy.filter;

import javax.servlet.http.HttpServletRequest;

public class SessionAuthFilter extends AbstractAuthFilter {

    public String getRemoteUser(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(currentUserKey);
    }
}
