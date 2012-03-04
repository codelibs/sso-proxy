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
    public static GetMethod buildGetMethod(String url,
            Map<String, String[]> getMethodParams, String encoding) {
        return new GetMethod(buildGetMethodUrl(url, getMethodParams, encoding));
    }

    public static String buildGetMethodUrl(String url,
            Map<String, String[]> getMethodParams, String encoding) {
        if (getMethodParams.isEmpty()) {
            return url;
        }

        boolean alreadyAdded = false;
        if (url.indexOf(SSOProxyConstants.REQUEST_PARAM_URL_SEPARATOR) >= 0) {
            alreadyAdded = true;
        }

        // request parameters
        StringBuilder query = new StringBuilder(url);
        for (Map.Entry<String, String[]> entry : getMethodParams.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            for (int i = 0; i < value.length; i++) {
                if (alreadyAdded) {
                    query
                            .append(SSOProxyConstants.REQUEST_PARAM_QUERY_SEPARATOR);
                } else {
                    query
                            .append(SSOProxyConstants.REQUEST_PARAM_URL_SEPARATOR);
                    alreadyAdded = true;
                }
                query.append(encode(key, encoding));
                query.append(SSOProxyConstants.REQUEST_PARAM_EQUAL);
                query.append(encode(value[i], encoding));
            }
        }
        return query.toString();
    }

    public static PostMethod buildPostMethod(String url,
            Map<String, String[]> postMethodParams, String encoding) {
        return buildPostMethod(url, new HashMap<String, String[]>(0),
                postMethodParams, encoding);
    }

    public static PostMethod buildPostMethod(String url,
            Map<String, String[]> getMethodParams,
            Map<String, String[]> postMethodParams, String encoding) {
        PostMethod postMethod = new PostMethod(buildGetMethodUrl(url,
                getMethodParams, encoding));
        postMethod.getParams().setContentCharset(encoding);
        // request parameters
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String[]> entry : postMethodParams.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            for (int i = 0; i < value.length; i++) {
                pairs.add(new NameValuePair(key, value[i]));
            }
        }
        postMethod.setRequestBody(pairs.toArray(new NameValuePair[0]));
        return postMethod;
    }

    private static String encode(String value, String encoding) {
        try {
            return URLEncoder.encode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
