package vinna;

import vinna.request.Request;
import vinna.route.*;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Vinna {

    private final Router router;
    private List<ActionArgument> routeParameters;

    public Vinna(String routesPath) throws UnsupportedEncodingException {
        this.router = new Router();
        List<Route> routes = new RoutesParser().loadFrom(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(routesPath), "utf-8"));
        router.addRoutes(routes);
    }

    public Vinna() {
        this.router = new Router();
        routes();
    }

    protected void routes() {
        //TODO: define default catchall routes
    }

    public RouteResolution match(Request request) {
        return router.match(request);
    }

    public final void addRoute(Route route) {
        this.router.addRoute(route);
    }

    protected final RouteBuilder get(String path) {
        // TODO exception
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
}
