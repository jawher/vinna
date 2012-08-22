package vinna.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class VinnaResponseWrapper extends HttpServletResponseWrapper {
    private final HttpServletResponse httpServletResponse;


    public VinnaResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        this.httpServletResponse = httpServletResponse;
    }

}
