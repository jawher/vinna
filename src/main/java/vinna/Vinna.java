package vinna;

import vinna.route.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Vinna {

    private final Router router;
	private RouteBuilder currentBuilder;

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
		currentBuilder = new RouteBuilder("GET", path, this);
        return currentBuilder;
    }

    protected final int getInt(String name) {
		currentBuilder.addArgument(name);
        return 0;
    }

	protected final String getString(String name) {
		currentBuilder.addArgument(name);
		return "";
	}

	protected final boolean getBoolean(String name) {
		currentBuilder.addArgument(name);
		return false;
	}
}
