package vinna.request;

import java.util.Collection;
import java.util.Map;

class MockedRequest implements Request {

    public String verb;
    public String path;
    public Map<String, Collection<String>> param;

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
        return param.get(name);
    }
}
