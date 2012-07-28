package vinna.outcome;

import vinna.request.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StringOutcome implements Outcome {

    private final String content;

    public StringOutcome(String content) {
        this.content = content;
    }

    @Override
    public void execute(Request request, HttpServletResponse response) {
        try {
            response.getOutputStream().write((content.getBytes("utf-8")));
        } catch (IOException e) {
            // aie
        }
    }
}
