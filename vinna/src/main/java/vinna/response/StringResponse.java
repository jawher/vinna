package vinna.response;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

import javax.servlet.ServletException;
import java.io.IOException;

public class StringResponse extends AbstractResponse {

    private final String content;
    private final String encoding;

    public StringResponse(String content) {
        this(content, "UTF-8");
    }

    public StringResponse(String content, String encoding) {
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
