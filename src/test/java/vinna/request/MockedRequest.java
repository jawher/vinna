package vinna.request;

import java.util.*;

class MockedRequest implements Request {

    private String verb;
    private String path;
    private Map<String, Collection<String>> params;

    MockedRequest(String verb, String path, Map<String, Collection<String>> params) {
        this.verb = verb;
        this.path = path;
        this.params = params;
    }

    @Override
    public String getVerb() {
        return verb;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Collection<String> getParam(String name) {
        return params.get(name);
    }

    public static Builder get(String path) {
        return new Builder("GET", path);
    }

    public static class Builder {
        private String verb;
        private String path;
        private Map<String, Collection<String>> params = new HashMap<>();

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

        public MockedRequest build() {
            return new MockedRequest(verb, path, params);
        }
    }
}
