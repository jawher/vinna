package vinna.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class VinnaResponseWrapper extends HttpServletResponseWrapper {
    private final HttpServletResponse httpServletResponse;
    private int status;

    public VinnaResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        this.httpServletResponse = httpServletResponse;
        this.httpServletResponse.setCharacterEncoding("utf-8");
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
        super.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.status = sc;
        super.setStatus(sc, sm);
    }

    public int getStatus() {
        return status;
    }
}
