package jp.sf.ssoproxy.cookie;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.cookie.MalformedCookieException;

public class StandardBrowserSpec extends CookieSpecBase {
    public StandardBrowserSpec() {
        super();
    }

    /**
     * Performs most common {@link Cookie} validation
     *
     * @param host the host from which the {@link Cookie} was received
     * @param port the port from which the {@link Cookie} was received
     * @param path the path from which the {@link Cookie} was received
     * @param secure <tt>true</tt> when the {@link Cookie} was received using a
     * secure connection
     * @param cookie The cookie to validate.
     * @throws MalformedCookieException if an exception occurs during
     * validation
     */

    public void validate(String host, int port, String path, boolean secure,
            final Cookie cookie) throws MalformedCookieException {

        LOG.trace("enter CookieSpecBase.validate("
                + "String, port, path, boolean, Cookie)");
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException(
                    "Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException(
                    "Path of origin may not be null.");
        }
        if (path.trim().equals("")) {
            path = PATH_DELIM;
        }
        host = host.toLowerCase();
        // check version
        if (cookie.getVersion() < 0) {
            throw new MalformedCookieException("Illegal version number "
                    + cookie.getValue());
        }

        // security check... we musn't allow the server to give us an
        // invalid domain scope

        // Validate the cookies domain attribute.  NOTE:  Domains without 
        // any dots are allowed to support hosts on private LANs that don't 
        // have DNS names.  Since they have no dots, to domain-match the 
        // request-host and domain must be identical for the cookie to sent 
        // back to the origin-server.
        if (host.indexOf(".") >= 0) {
            // Not required to have at least two dots.  RFC 2965.
            // A Set-Cookie2 with Domain=ajax.com will be accepted.

            // domain must match host
            if (!host.endsWith(cookie.getDomain())) {
                String s = cookie.getDomain();
                if (s.startsWith(".")) {
                    s = s.substring(1, s.length());
                }
                if (!host.equals(s)) {
                    throw new MalformedCookieException(
                            "Illegal domain attribute \"" + cookie.getDomain()
                                    + "\". Domain of origin: \"" + host + "\"");
                }
            }
        } else {
            if (!host.equals(cookie.getDomain())) {
                throw new MalformedCookieException(
                        "Illegal domain attribute \"" + cookie.getDomain()
                                + "\". Domain of origin: \"" + host + "\"");
            }
        }

        // another security check... we musn't allow the server to give us a
        // cookie that doesn't match this path

        int idx = path.lastIndexOf(PATH_DELIM);
        String requstPath = path;
        if (idx > 0) {
            requstPath = requstPath.substring(0, idx + 1);
        }
        if (cookie.getPath() != null && cookie.getPath().startsWith(requstPath)) {
            throw new MalformedCookieException("Illegal path attribute \""
                    + cookie.getPath() + "\". Path of origin: \"" + path + "\"");
        }
        //       if (!path.startsWith(cookie.getPath())) {
        //           throw new MalformedCookieException(
        //               "Illegal path attribute \"" + cookie.getPath() 
        //               + "\". Path of origin: \"" + path + "\"");
        //       }
    }
}
