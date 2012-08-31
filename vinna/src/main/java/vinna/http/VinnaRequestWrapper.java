package vinna.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class VinnaRequestWrapper extends HttpServletRequestWrapper implements Request {

    private final HttpServletRequest servletRequest;

    public VinnaRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
        this.servletRequest = servletRequest;
    }

    @Override
    public String getPath() {
        return servletRequest.getServletPath();
    }

    @Override
    public String getParameter(String name) {
        return servletRequest.getParameter(name);
    }

    @Override
    public Collection<String> getParameters(String name) {
        String[] parameterValues = servletRequest.getParameterValues(name);
        if (parameterValues != null) {
            return Arrays.asList(parameterValues);
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Collection<String>> getParameters() {
        Map<String, Collection<String>> params = new HashMap<>();

        Enumeration enumeration = servletRequest.getParameterNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String paramName = (String) enumeration.nextElement();
                params.put(paramName, getParameters(paramName));
            }
        }

        return Collections.unmodifiableMap(params);
    }

    @Override
    public Collection<String> getHeaderValues(String name) {
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
                headers.put(headerName, getHeaderValues(headerName));
            }
        }

        return Collections.unmodifiableMap(headers);
    }

    @Override
    public Map<String, Cookie> getCookiesMap() {
        Map<String, Cookie> cookies = new HashMap<>();
        for (javax.servlet.http.Cookie cookie : getCookies()) {
            cookies.put(cookie.getName(), new Cookie(cookie.getName(), cookie.getValue())
                    .withComment(cookie.getComment())
                    .withDomain(cookie.getDomain())
                    .withMaxAge(cookie.getMaxAge())
                    .withPath(cookie.getPath())
                    .withSecure(cookie.getSecure())
                    .withVersion(cookie.getVersion()));
        }
        return Collections.unmodifiableMap(cookies);
    }
}
