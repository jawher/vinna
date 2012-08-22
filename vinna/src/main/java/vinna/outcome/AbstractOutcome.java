package vinna.outcome;

import vinna.exception.VuntimeException;
import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public abstract class AbstractOutcome implements Outcome {

    private ResponseBuilder responseBuilder;

    public final ResponseBuilder withResponseBuilder(ResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
        return this.responseBuilder;
    }

    public final ResponseBuilder getResponseBuilder() {
        return this.responseBuilder;
    }

    public abstract void writeResponse(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException;

    @Override
    public final void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        if (responseBuilder != null) {
            responseBuilder.build().execute(request, response);
            writeResponse(request, response);
        } else {
            throw new VuntimeException("no ResponseBuilder defined");
        }
    }

}
