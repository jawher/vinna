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
        RouteResolution headResolution = null;
        for (Route route : routes) {
            RouteResolution routeResolution = route.match(request);
            if (routeResolution != null) {
                if (route.getVerb().equalsIgnoreCase(request.getMethod())) {
                    logger.debug("Route matched {}", route);
                    return routeResolution;
                } else if (route.getVerb().equalsIgnoreCase("GET") && request.getMethod().equalsIgnoreCase("HEAD")
                        && headResolution == null) {
                    logger.debug("Potential route matched {}", route);
                    headResolution = routeResolution;
                }
            }
        }

        return headResolution;
    }

    public void addRoutes(List<Route> routes) {
        this.routes.addAll(routes);
    }
}
