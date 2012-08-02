package vinna.outcome;

import vinna.request.Request;
import vinna.response.Response;

import java.io.IOException;

public class StringOutcome implements Outcome {

    private final String content;

    public StringOutcome(String content) {
        this.content = content;
    }

    @Override
    public void execute(Request request, Response response) throws IOException {
        response.setContentType("text/plain");
        response.getOutputStream().write((content.getBytes("utf-8")));
    }
}
