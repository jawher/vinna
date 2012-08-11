package vinna.route;

import vinna.request.Request;

import java.util.ArrayList;
import java.util.List;

public class Router {

    private final List<Route> routes = new ArrayList<>();

    public final void addRoute(Route route) {
        routes.add(route);
    }

    public RouteResolution match(Request request) {
        for (Route route : routes) {
            RouteResolution routeResolution = route.match(request);
            if (routeResolution != null) {
                return routeResolution;
            }
        }
        return null;
    }


    public void addRoutes(List<Route> routes) {
        this.routes.addAll(routes);
    }
}
