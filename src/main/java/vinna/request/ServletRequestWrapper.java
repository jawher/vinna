package vinna.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @author lpereira
 */
public class ServletRequestWrapper implements Request {

    private final HttpServletRequest servletRequest;

    public ServletRequestWrapper(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Override
    public String getVerb() {
        return servletRequest.getMethod();
    }

    @Override
    public String getPath() {
        return servletRequest.getServletPath();
    }

    @Override
    public String getParam(String name) {
        return servletRequest.getParameter(name);
    }
}
