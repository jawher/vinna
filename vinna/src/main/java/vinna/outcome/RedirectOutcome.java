package vinna.outcome;

import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public class RedirectOutcome implements Outcome {

    private final String location;

    public RedirectOutcome(String location) {
        this.location = location;
    }

    @Override
    public void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        String encodedLocation = response.encodeRedirectURL(location);
        response.sendRedirect(encodedLocation);
    }
}
