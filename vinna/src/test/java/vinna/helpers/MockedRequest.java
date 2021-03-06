package vinna.helpers;

import vinna.http.Cookie;
import vinna.http.Request;

import java.io.InputStream;
import java.util.*;

public class MockedRequest implements Request {

    private final String verb;
    private final String path;
    private final Map<String, Collection<String>> params;
    private final Map<String, Collection<String>> headers;
    private final Map<String, Cookie> cookies;

    private MockedRequest(String verb, String path, Map<String, Collection<String>> params, Map<String, Collection<String>> headers, Map<String, Cookie> cookies) {
        this.verb = verb;
        this.path = path;
        this.params = params;
        this.headers = headers;
        this.cookies = cookies;
    }

    @Override
    public String getMethod() {
        return verb;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getParameter(String name) {
        Collection<String> param = getParameters(name);
        if (param.size() == 0) {
            return null;
        }
        return param.iterator().next();
    }

    @Override
    public Collection<String> getParameters(String name) {
        if (params.get(name) == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(params.get(name));
    }

    @Override
    public Map<String, Collection<String>> getParameters() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public String getHeader(String name) {
        Collection<String> header = getHeaderValues(name);
        if (header.size() == 0) {
            return null;
        }
        return header.iterator().next();
    }

    @Override
    public Collection<String> getHeaderValues(String name) {
        if (headers.get(name) == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(headers.get(name));
    }

    @Override
    public Map<String, Collection<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public Map<String, Cookie> getCookiesMap() {

        return Collections.unmodifiableMap(cookies);
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    public static Builder get(String path) {
        return new Builder("GET", path);
    }

    public static Builder post(String path) {
        return new Builder("POST", path);
    }

    public static Builder head(String path) {
        return new Builder("HEAD", path);
    }

    public static class Builder {
        private final String verb;
        private final String path;
        private final Map<String, Collection<String>> params = new HashMap<>();
        private final Map<String, Collection<String>> headers = new HashMap<>();
        private final Map<String, Cookie> cookies = new HashMap<>();


        private Builder(String verb, String path) {
            this.verb = verb;
            this.path = path;
        }

        public Builder param(String name, String value) {
            params.put(name, Collections.singleton(value));
            return this;
        }

        public Builder param(String name, String... values) {
            params.put(name, Arrays.asList(values));
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(name, Collections.singleton(value));
            return this;
        }

        public Builder header(String name, String... values) {
            headers.put(name, Arrays.asList(values));
            return this;
        }

        public Builder cookie(Cookie cookie) {
            cookies.put(cookie.getName(), cookie);
            return this;
        }

        public MockedRequest build() {
            return new MockedRequest(verb, path, params, headers, cookies);
        }
    }
}
