package vinna.request;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    @Override
    public Collection<String> getParams(String name) {
        String[] parameterValues = servletRequest.getParameterValues(name);
        if (parameterValues != null) {
            return Arrays.asList(parameterValues);
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Collection<String>> getParams() {
        Map<String, Collection<String>> params = new HashMap<>();

        Enumeration enumeration = servletRequest.getParameterNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String paramName = (String) enumeration.nextElement();
                params.put(paramName, getParams(paramName));
            }
        }

        return Collections.unmodifiableMap(params);
    }

    @Override
    public String getHeader(String name) {
        return servletRequest.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        Enumeration<String> enumeration = servletRequest.getHeaders(name);
        if (enumeration != null) {
            return Collections.list(enumeration);
        }
        return Collections.emptyList();

    }

    @Override
    public Map<String, Collection<String>> getHeaders() {
        Map<String, Collection<String>> headers = new HashMap<>();

        Enumeration enumeration = servletRequest.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String headerName = (String) enumeration.nextElement();
                headers.put(headerName, getHeaders(headerName));
            }
        }

        return Collections.unmodifiableMap(headers);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return servletRequest.getInputStream();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return servletRequest.getRequestDispatcher(path);
    }

    @Override
    public void setAttribute(String name, Object object) {
        servletRequest.setAttribute(name, object);
    }

    @Override
    public Object getAttribute(String name) {
        return servletRequest.getAttribute(name);
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return servletRequest;
    }
}
