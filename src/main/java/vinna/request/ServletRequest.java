package vinna.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @author lpereira
 */
public class ServletRequest implements Request {

    private final HttpServletRequest servletRequest;

    public ServletRequest(HttpServletRequest servletRequest) {
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
    public Map<String, String> getParams() {
        return Collections.<String, String> unmodifiableMap(servletRequest.getParameterMap());
    }
}
