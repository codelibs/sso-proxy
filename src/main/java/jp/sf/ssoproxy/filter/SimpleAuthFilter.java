package jp.sf.ssoproxy.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.SSOProxyConstants;

import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthFilter extends SessionAuthFilter {
    private static final Logger logger = LoggerFactory
            .getLogger(SimpleAuthFilter.class);

    private final Map<String, String> usernamePasswordMap = new HashMap<String, String>();

    private String usernameKey;

    private String passwordKey;

    @Override
    public void init(final FilterConfig config) throws ServletException {
        super.init(config);

        final String value = config
                .getInitParameter(SSOProxyConstants.USERNAME_PASSWORD_MAP_KEY);
        if (StringUtil.isNotBlank(value)) {
            final String[] data = value.split("[\\s]");
            for (final String userPassword : data) {
                if (StringUtil.isNotBlank(userPassword)) {
                    final String[] pair = userPassword.trim().split(":");
                    if (pair.length == 2) {
                        usernamePasswordMap.put(pair[0], pair[1]);
                    } else {
                        logger.warn("Invalid user/password format: "
                                + userPassword);
                    }
                }
            }
        }

        usernameKey = config
                .getInitParameter(SSOProxyConstants.SIMPLE_USERNAME_KEY);
        if (StringUtil.isBlank(usernameKey)) {
            usernameKey = SSOProxyConstants.SIMPLE_USERNAME_KEY;
        }
        passwordKey = config
                .getInitParameter(SSOProxyConstants.SIMPLE_PASSWORD_KEY);
        if (StringUtil.isBlank(passwordKey)) {
            passwordKey = SSOProxyConstants.SIMPLE_PASSWORD_KEY;
        }
    }

    @Override
    public String getRemoteUser(final HttpServletRequest request) {
        final String remoteUser = super.getRemoteUser(request);
        if (remoteUser != null) {
            return remoteUser;
        }
        final String username = request.getParameter(usernameKey);
        final String password = request.getParameter(passwordKey);
        if (StringUtil.isNotEmpty(username) && StringUtil.isNotEmpty(password)) {
            final String p = usernamePasswordMap.get(username);
            if (password.equals(p)) {
                request.getSession().setAttribute(
                        SSOProxyConstants.CURRENT_REMOTE_USER, username);
                return username;
            }
        }
        return null;
    }

}
