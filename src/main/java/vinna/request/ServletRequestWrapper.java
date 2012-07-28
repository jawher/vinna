package vinna.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

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
    public Collection<String> getParam(String name) {
        return Arrays.asList(servletRequest.getParameterValues(name));
    }
}
