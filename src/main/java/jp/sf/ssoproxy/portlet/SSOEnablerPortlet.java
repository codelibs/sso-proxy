package jp.sf.ssoproxy.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import jp.sf.ssoproxy.SSOProxyConstraints;

public class SSOEnablerPortlet extends GenericPortlet {

    protected void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        String remoteUser = request.getRemoteUser();
        if (remoteUser != null) {
            request.getPortletSession().setAttribute(
                    SSOProxyConstraints.CURRENT_REMOTE_USER, remoteUser,
                    PortletSession.APPLICATION_SCOPE);
        }
    }

}
