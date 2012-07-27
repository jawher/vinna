package vinna.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lpereira
 */
public interface Request {

    String getVerb();

    String getPath();

    // FIXME: What about multivalued params ?
    String getParam(String name);
}
