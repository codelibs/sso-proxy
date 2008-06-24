package jp.sf.ssoproxy.util;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.security.UserManager;

public class JetspeedUtil {
    public static UserManager userManager;

    public static UserManager getUserManager() {
        if (userManager == null) {
            userManager = (UserManager) Jetspeed.getComponentManager()
                    .getComponent("org.apache.jetspeed.security.UserManager");
            if (userManager == null) {
                //TODO message
                throw new IllegalStateException();
            }
        }
        return userManager;
    }
}
