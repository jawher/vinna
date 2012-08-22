package vinna.outcome;

import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public class RedirectOutcome extends AbstractOutcome {

    public RedirectOutcome(String location) {
        this(location, 302);
    }

    public RedirectOutcome(String location, int status) {
        withResponseBuilder(ResponseBuilder.withStatus(status)).location(location);
    }

    @Override
    public void writeResponse(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        // nothing to do
    }
}
