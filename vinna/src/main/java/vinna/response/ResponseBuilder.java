package vinna.response;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.util.MultivaluedHashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ResponseBuilder implements Response {

    private int status;
    private MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    private String location;
    private InputStream body;
    private String encoding;

    public static ResponseBuilder withStatus(int status) {
        return new ResponseBuilder(status);
    }

    public static ResponseBuilder ok() {
        return new ResponseBuilder(HttpServletResponse.SC_OK);
    }

    public static ResponseBuilder notFound() {
        return new ResponseBuilder(HttpServletResponse.SC_NOT_FOUND);
    }

    public ResponseBuilder(int status) {
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

    public final ResponseBuilder encoding(String encoding) {
        this.encoding = encoding;
        header("Content-Encoding", encoding);
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

    //FIXME: split into addHeader and setHeader (maybe call the latter header)
    public final ResponseBuilder header(String name, Object value) {
        headers.add(name, value);
        return this;
    }

    // TODO define parameters
    public final ResponseBuilder cookie() {
        // TODO
        return this;
    }

    public ResponseBuilder body(InputStream body) {
        this.body = body;
        return this;
    }

    protected void writeBody(ServletOutputStream out) throws IOException {
        if (body != null) {
            int size = 512;//FIXME: make this configurable ?
            byte[] buffer = new byte[size];
            int len;
            while ((len = body.read(buffer)) == size) {
                out.write(buffer, 0, len);
            }
            try {
                body.close();
            } catch (IOException e) {
                //FIXME: issue warning
            }
        }
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

    public String getEncoding() {
        return encoding;
    }

    @Override
    public void execute(VinnaRequestWrapper request, VinnaResponseWrapper response) throws IOException, ServletException {
        response.setStatus(status);

        for (Map.Entry<String, List<Object>> header : headers.entrySet()) {
            for (Object value : header.getValue()) {
                //FIXME: properly handle multi-valued headers (using servletResponse.(add|set)Header)
                response.addHeader(header.getKey(), value.toString());
            }
        }

        // FIXME convert the Location to an absolute URL <-- is this really needed ?
        // FIXME: investigate how to properly handle redirect
        // FIXME: should use a proper redirect flag instead of just location presence
        if (location != null) {
            response.setHeader("Location", response.encodeRedirectURL(location));
            return;
        }

        if (encoding != null) {
            response.setCharacterEncoding(encoding);
        }

        writeBody(response.getOutputStream());
        response.getOutputStream().flush();
    }
}
