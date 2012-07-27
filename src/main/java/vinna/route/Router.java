package vinna.route;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private final List<Route> routes = new ArrayList<>();

    public final void addRoute(Route route) {
        routes.add(route);
    }

    public Route.RouteResolution match(HttpServletRequest request) {
        System.out.println("Try to match " + request.getServletPath());
        for (Route route : routes) {
            System.out.println("checking against " + route);
            if (route.hasVerb(request.getMethod())) {
                Route.RouteResolution routeResolution = route.match(request.getServletPath());
                if (routeResolution != null) {
                    return routeResolution;
                }
            }
        }
        return null;
    }




    public void addRoutes(List<Route> routes) {
        this.routes.addAll(routes);
    }
}
