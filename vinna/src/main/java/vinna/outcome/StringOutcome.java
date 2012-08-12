package vinna.outcome;

import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;

import java.io.IOException;

public class StringOutcome implements Outcome {

    private final String content;

    public StringOutcome(String content) {
        this.content = content;
    }

    @Override
    public void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException {
        response.setContentType("text/plain");
        response.getOutputStream().write((content.getBytes("utf-8")));
    }
}
