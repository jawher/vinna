package vinna;

import vinna.route.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Vinna {

    private final Router router;
    private List<Parameters> routeParameters;

    public Vinna(String routesPath) throws UnsupportedEncodingException {
        this();
        List<Route> routes = new RoutesParser().loadFrom(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(routesPath), "utf-8"));
        router.addRoutes(routes);
    }

    public Vinna() {
        this.router = new Router();
    }

    public RouteResolution match(HttpServletRequest request) {
        return router.match(request);
    }

    public void addRoute(Route route) {
        this.router.addRoute(route);
    }

    protected final RouteBuilder get(String path) {
        // TODO exception
        routeParameters = new ArrayList<>();
        return new RouteBuilder("GET", path, this, routeParameters);
    }

    protected final Parameters param(String name) {
        Parameters param = new Parameters(Parameters.Type.VARIABLE, name);
        routeParameters.add(param);
        return param;
    }

    protected final Parameters constant(String value) {
        Parameters param = new Parameters(Parameters.Type.CONSTANT, value);
        routeParameters.add(param);
        return param;
    }
}
