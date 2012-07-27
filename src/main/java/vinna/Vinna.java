package vinna;

import vinna.route.Route;
import vinna.route.RouteBuilder;
import vinna.route.Router;
import vinna.route.RoutesParser;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Vinna {

    private final Router router;

    public Vinna(String routesPath) throws UnsupportedEncodingException {
        this();
        List<Route> routes = new RoutesParser().loadFrom(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(routesPath), "utf-8"));
        router.addRoutes(routes);

    }

    public Vinna() {
        this.router = new Router();
    }

    public Route.RouteResolution match(HttpServletRequest request) {
        return router.match(request);
    }

    public Collection<Route> getRoutes() {
        return Collections.unmodifiableCollection(router.getRoutes());
    }

    public void addRoute(Route route) {
        this.router.addRoute(route);
    }

    protected final RouteBuilder get(String path) {
        // TODO exception
        return new RouteBuilder("GET", path, this);
    }

    protected final int getInt(String name) {
        // TODO
        return 0;
    }
}
