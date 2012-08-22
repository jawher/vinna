package vinna;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;

public class VinnaContext {
    private static ThreadLocal<VinnaContext> context = new ThreadLocal<>();

    public final Vinna vinna;
    public final VinnaRequestWrapper request;
    public final VinnaResponseWrapper response;

    public VinnaContext(Vinna vinna, VinnaRequestWrapper request, VinnaResponseWrapper response) {
        this.vinna = vinna;
        this.request = request;
        this.response = response;
    }

    public static VinnaContext get() {
        return context.get();
    }

    static void set(VinnaContext c) {
        context.set(c);
    }
}
