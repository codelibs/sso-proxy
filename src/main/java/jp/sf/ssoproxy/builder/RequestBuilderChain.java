package jp.sf.ssoproxy.builder;

import javax.servlet.http.HttpServletRequest;

import jp.sf.ssoproxy.config.HostConfig;

import org.apache.commons.httpclient.HttpMethod;

public class RequestBuilderChain {
    private RequestBuilder[] requestBuilders;

    private ThreadLocal<Integer> position = new ThreadLocal<Integer>();

    private int size = 0;

    public void reset() {
        position.set(new Integer(0));
    }

    public void build(int type, HttpServletRequest request,
            HostConfig hostConfig, HttpMethod httpMethod) {
        // Call the next filter if there is one
        int pos = position.get().intValue();
        if (pos < size) {
            RequestBuilder requestBuilder = requestBuilders[pos++];
            position.set(new Integer(pos));
            requestBuilder.build(type, request, hostConfig, httpMethod, this);
            position.set(new Integer(--pos));
            return;
        }

    }

    public RequestBuilder[] getRequestBuilders() {
        return requestBuilders;
    }

    public void setRequestBuilders(RequestBuilder[] requestBuilders) {
        this.requestBuilders = requestBuilders;
        size = requestBuilders.length;
    }
}
