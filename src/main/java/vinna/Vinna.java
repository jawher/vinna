package vinna;

import vinna.request.Request;
import vinna.route.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Vinna {

    private final Router router;
    private List<ActionArgument> routeParameters;
    private ControllerFactory controllerFactory;

    public Vinna(Reader routesReader) {
        this.controllerFactory = controllerFactory();
        this.router = new Router();
        List<Route> routes = new RoutesParser().loadFrom(routesReader, this);
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

    public Object createController(String id, Class<?> clazz) {
        return controllerFactory.create(id, clazz);
    }

    public RouteResolution match(Request request) {
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

    protected final String header(String name) {
        ActionArgument param = new ActionArgument.Header(name);
        routeParameters.add(param);
        return name;
    }

    protected final Collection<String> headers(String name) {
        ActionArgument param = new ActionArgument.Headers(name);
        routeParameters.add(param);
        return Collections.emptyList();
    }
}
