package vinna.outcome;

import vinna.request.Request;
import vinna.response.Response;

import java.io.IOException;

public interface Outcome {
    public void execute(Request request, Response response) throws IOException;
}
