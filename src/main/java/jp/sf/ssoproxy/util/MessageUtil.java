package jp.sf.ssoproxy.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {
    private static final String RESOURCE_MESSAGES = "jp.sf.ssoproxy.resource.Messages";

    public static ResourceBundle getResourceBundle(Locale locale) {
        // TODO cache
        return ResourceBundle.getBundle(RESOURCE_MESSAGES, locale);
    }

    public static String getMessage(String key, Locale locale) {
        return getMessage(key, null, locale);
    }

    public static String getMessage(String key, Object[] args, Locale locale) {
        try {
            String msg = getResourceBundle(locale).getString(key);
            if (args != null) {
                msg = MessageFormat.format(msg, args);
            }
            return msg;
        } catch (Exception e) {
            return key;
        }
    }
}
