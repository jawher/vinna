package vinna.route;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import vinna.Vinna;
import vinna.outcome.Outcome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class RouteBuilder {
    private final String path;
    private final String verb;
    private final Vinna context;
    private final List<Parameters> parameters;

    private Class controller;
    private Method method;
    private String controllerId;//FIXME: expose a way to set this

    public RouteBuilder(String verb, String path, Vinna context, List<Parameters> parameters) {
        this.path = path;
        this.context = context;
        this.verb = verb;
        this.parameters = parameters;
    }

    public <T> T withController(Class<T> controller) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        this.controller = controller;

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(controller);

        // TODO constructor with params
        T proxy = (T) factory.create(new Class<?>[0], new Object[0], new RouteMethodHandler());
        return proxy;
    }

    private Route createRoute() {
        RoutesParser.ParsedPath parsedPath = RoutesParser.parsePath(path);
        Route.Action action = new Route.Action(controllerId, controller, method, parameters);
        return new Route(verb, parsedPath.pathPattern, parsedPath.queryMap, parsedPath.variableNames, action);
    }

    private class RouteMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            // be careful with the method finalize
            System.err.println(thisMethod + ":" + thisMethod.getReturnType());
            if (method == null) {
                if (Outcome.class.isAssignableFrom(thisMethod.getReturnType())) {
                    method = thisMethod;
                    context.addRoute(createRoute());
                } else {
                    throw new Exception("Sorry, witchery is only available at Poudlard");
                }
            }
            return null;
        }
    }


}
