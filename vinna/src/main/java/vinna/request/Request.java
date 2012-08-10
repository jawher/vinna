package vinna.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface Request {

    String getVerb();

    String getPath();

    String getParam(String name);

    Collection<String> getParams(String name);

    Map<String, Collection<String>> getParams();

    String getHeader(String name);

    Collection<String> getHeaders(String name);

    Map<String, Collection<String>> getHeaders();

    InputStream getInputStream() throws IOException;
}
