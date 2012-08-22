package vinna.http;

import vinna.exception.VuntimeException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.UnsupportedEncodingException;

public class VinnaResponseWrapper extends HttpServletResponseWrapper {
    private final HttpServletResponse httpServletResponse;


    public VinnaResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        this.httpServletResponse = httpServletResponse;
        this.httpServletResponse.setCharacterEncoding("utf-8");
    }

}
