package jp.sf.ssoproxy.helper;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageHelper {
    public String resourceBundelName = "jp.sf.ssoproxy.resource.Messages";

    public ResourceBundle getResourceBundle(final Locale locale) {
        // TODO cache
        return ResourceBundle.getBundle(resourceBundelName, locale);
    }

    public String getMessage(final String key, final Locale locale) {
        return getMessage(key, null, locale);
    }

    public String getMessage(final String key, final Object[] args,
            final Locale locale) {
        try {
            String msg = getResourceBundle(locale).getString(key);
            if (args != null) {
                msg = MessageFormat.format(msg, args);
            }
            return msg;
        } catch (final Exception e) {
            return key;
        }
    }
}
