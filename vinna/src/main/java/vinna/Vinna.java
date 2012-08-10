package vinna;

import vinna.request.Request;
import vinna.route.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

public class Vinna {

    private final Router router;
    private ControllerFactory controllerFactory;

    private List<ActionArgument> routeParameters;
    protected final RequestBuilder req = new RequestBuilder();

    public Vinna(Reader routesReader) {
        this.controllerFactory = controllerFactory();
        this.router = new Router();
        List<Route> routes = new RoutesParser(routesReader).load();
        router.addRoutes(routes);
    }

    public Vinna() {
        this.controllerFactory = controllerFactory();
        this.router = new Router();
        routes();
    }

    /**
     * Override to define the app routes
     */
    protected void routes() {
        //TODO: define default catchall routes
    }

    /**
     * override to provide a custom controller factory
     *
     * @return a ... you guessed right, a controller factory
     */
    protected ControllerFactory controllerFactory() {
        return new DefaultControllerFactory();
    }

    public final Object createController(String id, Class<?> clazz) {
        return controllerFactory.create(id, clazz);
    }

    public final RouteResolution match(Request request) {
        return router.match(request);
    }

    public final void addRoute(Route route) {
        this.router.addRoute(route);
    }

    protected final RouteBuilder get(String path) {
        routeParameters = new ArrayList<>();
        return new RouteBuilder("GET", path, this, routeParameters);
    }

    protected final RouteBuilder post(String path) {
        routeParameters = new ArrayList<>();
        return new RouteBuilder("POST", path, this, routeParameters);
    }

    protected final ActionArgument.Variable param(String name) {
        ActionArgument.Variable param = new ActionArgument.Variable(name);
        routeParameters.add(param);
        return param;
    }

    protected final <T> T constant(T value) {
        ActionArgument param = new ActionArgument.Const<>(value);
        routeParameters.add(param);
        return value;
    }

    protected final class RequestBuilder {

        private RequestBuilder() {
        }

        public final ActionArgument.Header header(String name) {
            ActionArgument.Header param = new ActionArgument.Header(name);
            routeParameters.add(param);
            return param;
        }

        public final Map<String, Collection<String>> headers() {
            ActionArgument.Headers headers = new ActionArgument.Headers();
            routeParameters.add(headers);
            return new HashMap<>();
        }

        public final ActionArgument.RequestParameter param(String name) {
            ActionArgument.RequestParameter param = new ActionArgument.RequestParameter(name);
            routeParameters.add(param);
            return param;
        }

        public final Map<String, Collection<String>> params() {
            ActionArgument.RequestParameters headers = new ActionArgument.RequestParameters();
            routeParameters.add(headers);
            return new HashMap<>();
        }

        public final InputStream body() {
            ActionArgument.RequestBody bodyActionArgument = new ActionArgument.RequestBody();
            routeParameters.add(bodyActionArgument);
            return new InputStream() {

                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }
    }
}
