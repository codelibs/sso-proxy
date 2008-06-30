package jp.sf.ssoproxy.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieAuthFilter extends AbstractAuthFilter {

    public String getRemoteUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (currentUserKey.equals(cookies[i].getName())) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }
}
