package vinna.request;

import java.util.Collection;

public interface Request {

    String getVerb();

    String getPath();

    Collection<String> getParam(String name);
}
