package vinna.outcome;

import vinna.request.Request;
import vinna.response.Response;

import javax.servlet.ServletException;
import java.io.IOException;

public class RedirectOutcome implements Outcome {

    private final String location;

    public RedirectOutcome(String location) {
        this.location = location;
    }

    @Override
    public void execute(Request request, Response response) throws IOException, ServletException {
        String encodedLocation = response.getHttpServletResponse().encodeRedirectURL(location);
        response.getHttpServletResponse().sendRedirect(encodedLocation);
    }
}
