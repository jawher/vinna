package vinna.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServletResponseWrapper implements Response {

    private final HttpServletResponse httpServletResponse;

    private int status;
    private String contentType;
    private Map<String, Collection<String>> headers;

    public ServletResponseWrapper(HttpServletResponse httpServletResponse) {
        this.headers = new HashMap<>();
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return httpServletResponse.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return httpServletResponse.getWriter();
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
        httpServletResponse.setContentType(contentType);
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
        httpServletResponse.setStatus(status);
    }
}
