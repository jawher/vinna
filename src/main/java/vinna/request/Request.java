package vinna.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lpereira
 *
 */
public interface Request {

    String getVerb();

    String getPath();

    Map<String, String> getParams();
}
