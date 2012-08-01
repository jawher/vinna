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
    private final List<ActionArgument> parameters;

    private Class controller;
    private Method method;
    private String controllerId;//FIXME: expose a way to set this

    public RouteBuilder(String verb, String path, Vinna context, List<ActionArgument> parameters) {
        this.path = path;
        this.context = context;
        this.verb = verb;
        this.parameters = parameters;
    }

    public <T> T withController(Class<T> controller) {
        this.controller = controller;

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(controller);

        // TODO constructor with params
        T proxy = null;
        try {
            proxy = (T) factory.create(new Class<?>[0], new Object[0], new RouteMethodHandler());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return proxy;
    }

    private Route createRoute() {
        RoutesParser.ParsedPath parsedPath = RoutesParser.parsePath(path);
        Route.Action action = new Route.Action(controllerId, controller, method, parameters);
        return new Route(verb, parsedPath.pathPattern, parsedPath.queryMap, parsedPath.variableNames, action, context);
    }

    private class RouteMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            // be careful with the method finalize
            if (method == null) {
                if (Outcome.class.isAssignableFrom(thisMethod.getReturnType())) {
                    if (parameters.size() != args.length) {
                        throw new IllegalStateException("Like, really ?");
                    }
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
