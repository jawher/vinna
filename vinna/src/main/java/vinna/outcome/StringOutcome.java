package vinna.outcome;

import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public class StringOutcome extends AbstractOutcome {

    private final String content;
    private final String encoding;

    public StringOutcome(String content) {
        this(content, "UTF-8");
    }

    public StringOutcome(String content, String encoding) {
        withResponseBuilder(ResponseBuilder.ok()).type("text/plain");

        this.content = content;
        this.encoding = encoding;
    }

    @Override
    public void writeResponse(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        response.setCharacterEncoding(encoding);
        response.getOutputStream().write(((String) content).getBytes(encoding));
    }
}
