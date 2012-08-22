package vinna.response;

import javax.servlet.http.HttpServletResponse;

public class RedirectResponse extends ResponseBuilder {

    public RedirectResponse(String location) {
        this(location, HttpServletResponse.SC_FOUND);
    }

    public RedirectResponse(String location, int status) {
        super(status);
        location(location);
    }

}
