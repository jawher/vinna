package vinna.outcome;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.util.MultivaluedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    private int status;
    private MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    private String location;

    public static ResponseBuilder withStatus(int status) {
        return new ResponseBuilder(status);
    }

    public static ResponseBuilder ok() {
        return new ResponseBuilder(HttpServletResponse.SC_OK);
    }

    public static ResponseBuilder notFound() {
        return new ResponseBuilder(HttpServletResponse.SC_NOT_FOUND);
    }

    private ResponseBuilder(int status) {
        status(status);
    }

    public final ResponseBuilder status(int status) {
        this.status = status;
        return this;
    }

    public final ResponseBuilder type(String type) {
        header("Content-Type", type);
        return this;
    }

    public final ResponseBuilder language(String language) {
        header("Content-Language", language);
        return this;
    }

    public final ResponseBuilder variant(String variant) {
        header("Vary", variant);
        return this;
    }

    public final ResponseBuilder location(String location) {
        this.location = location;
        return this;
    }

    public final ResponseBuilder etag(String etag) {
        header("ETag", etag);
        return this;
    }

    public final ResponseBuilder lastModified(Date lastModified) {
        header("Last-Modified", lastModified);
        return this;
    }

    public final ResponseBuilder cacheControl(String cacheControl) {
        header("Cache-Control", cacheControl);
        return this;
    }

    public final ResponseBuilder expires(Date expires) {
        header("Expires", expires);
        return this;
    }

    public final ResponseBuilder header(String name, Object value) {
        headers.add(name, value);
        return this;
    }

    // TODO define parameters
    public final ResponseBuilder cookie() {
        // TODO
        return this;
    }

    public final Outcome build() {
        return new Outcome() {
            @Override
            public void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
                response.setStatus(status);

                for (Map.Entry<String, List<Object>> header : headers.entrySet()) {
                    for (Object value : header.getValue()) {
                        response.addHeader(header.getKey(), value.toString());
                    }
                }

                if (location != null) {
                    response.setHeader("Location", response.encodeRedirectURL(location));
                }
            }
        };
    }

    public final int getStatus() {
        return status;
    }

    public final Object getFirstHeader(String header) {
        return headers.getFirst(header);
    }

    public final List<Object> getHeaders(String header) {
        return headers.get(header);
    }

}
