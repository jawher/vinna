package vinna.response;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public abstract class AbstractResponse extends ResponseBuilder implements Response {
    public AbstractResponse(int status) {
        super(status);
    }

    @Override
    public final void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        build().execute(request, response);
    }

}
