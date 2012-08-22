package vinna.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.http.Request;

import java.util.ArrayList;
import java.util.List;

public class Router {
    private final static Logger logger = LoggerFactory.getLogger(Router.class);

    private final List<Route> routes = new ArrayList<>();

    public final void addRoute(Route route) {
        routes.add(route);
    }

    public RouteResolution match(Request request) {
        logger.debug("Resolving '{} {}'", request.getMethod(), request.getPath());
        for (Route route : routes) {
            RouteResolution routeResolution = route.match(request);
            if (routeResolution != null) {
                logger.debug("Route matched {}", route);
                return routeResolution;
            }
        }
        logger.debug("Unable to resolve '{} {}'", request.getMethod(), request.getPath());
        return null;
    }

    public void addRoutes(List<Route> routes) {
        this.routes.addAll(routes);
    }
}
