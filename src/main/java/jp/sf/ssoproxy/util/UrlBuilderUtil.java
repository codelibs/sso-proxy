package jp.sf.ssoproxy.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.ssoproxy.SSOProxyConstants;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class UrlBuilderUtil {
    public static GetMethod buildGetMethod(final String url,
            final Map<String, String[]> getMethodParams, final String encoding) {
        return new GetMethod(buildGetMethodUrl(url, getMethodParams, encoding));
    }

    private static String buildGetMethodUrl(final String url,
            final Map<String, String[]> getMethodParams, final String encoding) {
        if (getMethodParams.isEmpty()) {
            return url;
        }

        boolean alreadyAdded = false;
        if (url.indexOf(SSOProxyConstants.REQUEST_PARAM_URL_SEPARATOR) >= 0) {
            alreadyAdded = true;
        }

        // request parameters
        final StringBuilder query = new StringBuilder(url);
        for (final Map.Entry<String, String[]> entry : getMethodParams
                .entrySet()) {
            final String key = entry.getKey();
            final String[] value = entry.getValue();
            for (final String element : value) {
                if (alreadyAdded) {
                    query.append(SSOProxyConstants.REQUEST_PARAM_QUERY_SEPARATOR);
                } else {
                    query.append(SSOProxyConstants.REQUEST_PARAM_URL_SEPARATOR);
                    alreadyAdded = true;
                }
                query.append(encode(key, encoding));
                query.append(SSOProxyConstants.REQUEST_PARAM_EQUAL);
                query.append(encode(element, encoding));
            }
        }
        return query.toString();
    }

    public static PostMethod buildPostMethod(final String url,
            final Map<String, String[]> postMethodParams, final String encoding) {
        return buildPostMethod(url, new HashMap<String, String[]>(0),
                postMethodParams, encoding);
    }

    public static PostMethod buildPostMethod(final String url,
            final Map<String, String[]> getMethodParams,
            final Map<String, String[]> postMethodParams, final String encoding) {
        final PostMethod postMethod = new PostMethod(buildGetMethodUrl(url,
                getMethodParams, encoding));
        postMethod.getParams().setContentCharset(encoding);
        // request parameters
        final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (final Map.Entry<String, String[]> entry : postMethodParams
                .entrySet()) {
            final String key = entry.getKey();
            final String[] value = entry.getValue();
            for (final String element : value) {
                pairs.add(new NameValuePair(key, element));
            }
        }
        postMethod.setRequestBody(pairs.toArray(new NameValuePair[0]));
        return postMethod;
    }

    private static String encode(final String value, final String encoding) {
        try {
            return URLEncoder.encode(value, encoding);
        } catch (final UnsupportedEncodingException e) {
            return value;
        }
    }
}
