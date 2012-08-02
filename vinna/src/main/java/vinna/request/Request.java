package vinna.request;

import java.util.Collection;
import java.util.Map;

public interface Request {

    String getVerb();

    String getPath();

    Collection<String> getParam(String name);

    String getHeader(String name);

    Collection<String> getHeaders(String name);

    Map<String, Collection<String>> getHeaders();
}
