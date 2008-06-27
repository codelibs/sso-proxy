package jp.sf.ssoproxy.config.impl;

import java.util.prefs.Preferences;

import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.SSOProxyConstraints;
import jp.sf.ssoproxy.config.ConfigException;
import jp.sf.ssoproxy.util.JetspeedUtil;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;

public class JetspeedAuthConfigImpl extends AbstractAuthConfig {
    private String usernameKey;

    private String passwordKey;

    private String usernameAttributeKey;

    private String passwordAttributeKey;

    @Override
    protected String getDataValue(HttpServletRequest request, String value)
            throws ConfigException {
        if (usernameKey.equals(value)) {
            // from user attribute
            String remoteUser = (String) request.getSession().getAttribute(
                    SSOProxyConstraints.CURRENT_REMOTE_USER);
            if (remoteUser == null) {
                // error    
                throw new ConfigException("000009");
            }

            try {
                UserManager userManager = JetspeedUtil.getUserManager();
                User user = userManager.getUser(remoteUser);
                Preferences prefs = user.getUserAttributes();
                return prefs.get(usernameAttributeKey, "");
            } catch (SecurityException e) {
                // error    
                throw new ConfigException("000010", new Object[] { remoteUser });
            }
        } else if (passwordKey.equals(value)) {
            // from user attribute
            String remoteUser = (String) request.getSession().getAttribute(
                    SSOProxyConstraints.CURRENT_REMOTE_USER);
            if (remoteUser == null) {
                // error    
                throw new ConfigException("000011");
            }

            try {
                UserManager userManager = JetspeedUtil.getUserManager();
                User user = userManager.getUser(remoteUser);
                Preferences prefs = user.getUserAttributes();
                return prefs.get(passwordAttributeKey, "");
            } catch (SecurityException e) {
                // error    
                throw new ConfigException("000012", new Object[] { remoteUser });
            }
        }
        return value;
    }

    public String getUsernameKey() {
        return usernameKey;
    }

    public void setUsernameKey(String usernameKey) {
        this.usernameKey = usernameKey;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getUsernameAttributeKey() {
        return usernameAttributeKey;
    }

    public void setUsernameAttributeKey(String usernameAttributeKey) {
        this.usernameAttributeKey = usernameAttributeKey;
    }

    public String getPasswordAttributeKey() {
        return passwordAttributeKey;
    }

    public void setPasswordAttributeKey(String passwordAttributeKey) {
        this.passwordAttributeKey = passwordAttributeKey;
    }

}
