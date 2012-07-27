package vinna.route;

import vinna.request.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private final List<Route> routes = new ArrayList<>();

    public final void addRoute(Route route) {
        routes.add(route);
    }

    public RouteResolution match(Request request) {
        System.out.println("Try to match " + request.getPath());
        for (Route route : routes) {
            System.out.println("checking against " + route);
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
