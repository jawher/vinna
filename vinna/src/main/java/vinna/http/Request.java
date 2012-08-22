package vinna.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface Request {

    String getMethod();

    String getPath();

    String getParam(String name);

    Collection<String> getParams(String name);

    Map<String, Collection<String>> getParams();

    String getHeader(String name);

    Collection<String> getHeaderValues(String name);

    Map<String, Collection<String>> getHeaders();

    void setAttribute(String name, Object value);

    Object getAttribute(String name);

    InputStream getInputStream() throws IOException;
}
