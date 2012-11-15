package vinna;

import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.response.Response;
import vinna.route.RouteResolution;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

public class VinnaContext {
    private static ThreadLocal<VinnaContext> context = new ThreadLocal<>();

    public final Vinna vinna;
    public final VinnaRequestWrapper request;
    public final VinnaResponseWrapper response;
    public final ServletContext servletContext;
    public final Session session;

    RouteResolution routeResolution;
    private boolean canAbort = true;
    private Response abortedResponse;

    VinnaContext(Vinna vinna, VinnaRequestWrapper request, VinnaResponseWrapper response, ServletContext servletContext, Session session) {
        this.vinna = vinna;
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
        this.session = session;
    }

    public boolean isResolved() {
        return routeResolution != null;
    }

    public void abortWith(Response response) {
        if (canAbort) {
            this.abortedResponse = response;
        } else {
            throw new IllegalStateException("Response already forged");
        }
    }

    boolean isAborted() {
        return abortedResponse != null;
    }

    void canAbort(boolean canAbort) {
        this.canAbort = canAbort;
    }

    void sendResponse() throws IOException, ServletException {
        // TODO npe
        abortedResponse.execute(request, response);
    }

    public static VinnaContext get() {
        return context.get();
    }

    static void set(VinnaContext c) {
        context.set(c);
    }
}
