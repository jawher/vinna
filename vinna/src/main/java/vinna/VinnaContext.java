package vinna;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

import javax.servlet.ServletContext;

public class VinnaContext {
    private static ThreadLocal<VinnaContext> context = new ThreadLocal<>();

    public final Vinna vinna;
    public final VinnaRequestWrapper request;
    public final VinnaResponseWrapper response;
    public final ServletContext servletContext;

    public VinnaContext(Vinna vinna, VinnaRequestWrapper request, VinnaResponseWrapper response, ServletContext servletContext) {
        this.vinna = vinna;
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public static VinnaContext get() {
        return context.get();
    }

    static void set(VinnaContext c) {
        context.set(c);
    }
}
