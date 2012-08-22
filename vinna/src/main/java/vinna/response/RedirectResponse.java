package vinna.response;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectResponse extends AbstractResponse {

    public RedirectResponse(String location) {
        this(location, HttpServletResponse.SC_FOUND);
    }

    public RedirectResponse(String location, int status) {
        withResponseBuilder(ResponseBuilder.withStatus(status)).location(location);
    }

    @Override
    public void writeResponse(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        // nothing to do
    }
}
