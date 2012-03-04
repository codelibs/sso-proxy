package jp.sf.ssoproxy.config.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.SSOProxyConstants;
import jp.sf.ssoproxy.config.AuthConfig;
import jp.sf.ssoproxy.config.ConfigException;
import jp.sf.ssoproxy.util.UrlBuilderUtil;

import org.apache.commons.httpclient.HttpMethod;

public abstract class AbstractAuthConfig implements AuthConfig {

    private static final String DATA_NAME = "name";

    private static final String DATA_VALUE = "value";

    private static final String DATA_METHOD = "method";

    protected String authPageMethod;

    protected String authPageUrl;

    protected String authPageEncoding;

    protected List<Map<String, String>> authPageDataList; // method,name,value in Map.

    protected String loginPageMethod;

    protected String loginPageUrl;

    protected String loginPageEncoding;

    protected List<Map<String, String>> loginPageDataList; // method,name,value in Map.

    protected String loginPageKey;

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.config.impl.AuthConfig#checkLoginPageUrl(java.lang.String)
     */
    @Override
    public boolean checkLoginPageUrl(final String method, final String url,
            final Map<String, String[]> params) {
        if (url != null && url.equals(loginPageUrl)
                && loginPageMethod.equals(method)) {
            if (loginPageDataList != null) {
                for (final Map<String, String> map : loginPageDataList) {
                    final String name = map.get(DATA_NAME);
                    final String value = map.get(DATA_VALUE);
                    final String[] values = params.get(name);
                    if (value == null || values == null) {
                        return false;
                    } else if (values.length == 0) {
                        return false;
                    } else if (!value.equals(values[0])) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean checkLoginPage(final InputStream inputStream) {
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, loginPageEncoding));
            String line = reader.readLine();
            while (line != null) {
                if (line.indexOf(loginPageKey) >= 0) {
                    return true;
                }
                line = reader.readLine();
            }
            //        } catch (UnsupportedEncodingException e) {
            //        } catch (IOException e) {
        } catch (final Exception e) {
            // error
            throw new ConfigException("000013", e);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.config.impl.AuthConfig#buildLoginHttpMethod()
     */
    @Override
    public HttpMethod buildLoginHttpMethod(final HttpServletRequest request) {
        if (SSOProxyConstants.POST_METHOD.equals(loginPageMethod)) {
            return UrlBuilderUtil.buildPostMethod(
                    loginPageUrl,
                    getParameterMap(request, SSOProxyConstants.GET_METHOD,
                            loginPageDataList),
                    getParameterMap(request, SSOProxyConstants.POST_METHOD,
                            loginPageDataList), loginPageEncoding);
        } else if (SSOProxyConstants.GET_METHOD.equals(loginPageMethod)) {
            return UrlBuilderUtil.buildGetMethod(
                    loginPageUrl,
                    getParameterMap(request, SSOProxyConstants.GET_METHOD,
                            loginPageDataList), loginPageEncoding);
        }
        // error
        throw new ConfigException("000014", new Object[] { loginPageUrl });
    }

    /* (non-Javadoc)
     * @see jp.sf.ssoproxy.config.impl.AuthConfig#buildAuthHttpMethod()
     */
    @Override
    public HttpMethod buildAuthHttpMethod(final HttpServletRequest request) {
        if (SSOProxyConstants.POST_METHOD.equals(authPageMethod)) {
            return UrlBuilderUtil.buildPostMethod(
                    authPageUrl,
                    getParameterMap(request, SSOProxyConstants.GET_METHOD,
                            authPageDataList),
                    getParameterMap(request, SSOProxyConstants.POST_METHOD,
                            authPageDataList), authPageEncoding);
        } else if (SSOProxyConstants.GET_METHOD.equals(authPageMethod)) {
            return UrlBuilderUtil.buildGetMethod(
                    authPageUrl,
                    getParameterMap(request, SSOProxyConstants.GET_METHOD,
                            authPageDataList), authPageEncoding);
        }
        // error
        throw new ConfigException("000015", new Object[] { authPageUrl });
    }

    protected Map<String, String[]> getParameterMap(
            final HttpServletRequest request, final String method,
            final List<Map<String, String>> dataList) {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (dataList != null) {
            for (final Map<String, String> entry : dataList) {
                if (method.equals(entry.get(DATA_METHOD))) {
                    // TODO support 1 parameter only now..
                    final String[] values = new String[1];
                    values[0] = getDataValue(request, entry.get(DATA_VALUE));
                    params.put(entry.get(DATA_NAME), values);
                }
            }
        }
        return params;
    }

    protected abstract String getDataValue(HttpServletRequest request,
            String value);

    public String getAuthPageMethod() {
        return authPageMethod;
    }

    public void setAuthPageMethod(final String authPageMethod) {
        this.authPageMethod = authPageMethod;
    }

    public String getAuthPageUrl() {
        return authPageUrl;
    }

    public void setAuthPageUrl(final String authPageUrl) {
        this.authPageUrl = authPageUrl;
    }

    public String getAuthPageEncoding() {
        return authPageEncoding;
    }

    public void setAuthPageEncoding(final String authPageEncoding) {
        this.authPageEncoding = authPageEncoding;
    }

    public List<Map<String, String>> getAuthPageDataList() {
        return authPageDataList;
    }

    public void setAuthPageDataList(
            final List<Map<String, String>> authPageDataList) {
        this.authPageDataList = authPageDataList;
    }

    public String getLoginPageMethod() {
        return loginPageMethod;
    }

    public void setLoginPageMethod(final String loginPageMethod) {
        this.loginPageMethod = loginPageMethod;
    }

    public String getLoginPageUrl() {
        return loginPageUrl;
    }

    public void setLoginPageUrl(final String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }

    public String getLoginPageEncoding() {
        return loginPageEncoding;
    }

    public void setLoginPageEncoding(final String loginPageEncoding) {
        this.loginPageEncoding = loginPageEncoding;
    }

    public List<Map<String, String>> getLoginPageDataList() {
        return loginPageDataList;
    }

    public void setLoginPageDataList(
            final List<Map<String, String>> loginPageDataList) {
        this.loginPageDataList = loginPageDataList;
    }

    public String getLoginPageKey() {
        return loginPageKey;
    }

    public void setLoginPageKey(final String loginPageKey) {
        this.loginPageKey = loginPageKey;
    }

}
