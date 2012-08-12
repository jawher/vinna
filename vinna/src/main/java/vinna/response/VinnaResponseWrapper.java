package vinna.response;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

public class VinnaResponseWrapper extends HttpServletResponseWrapper {
    private final HttpServletResponse httpServletResponse;


    public VinnaResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        this.httpServletResponse = httpServletResponse;
    }

}
