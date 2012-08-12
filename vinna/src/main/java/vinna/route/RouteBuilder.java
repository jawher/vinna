package vinna.route;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import vinna.Vinna;
import vinna.exception.ConfigException;
import vinna.outcome.Outcome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RouteBuilder {

    private static final Pattern METHOD_PATTERN = Pattern.compile("(?<method>[^\\.]+)\\s*\\((?<args>.*)\\)");

    private final String path;
    private final String verb;
    private final Vinna context;
    private final List<ActionArgument> methodParameters;

    private final Map<String, Pattern> mandatoryQueryParameters;
    private final Map<String, Pattern> mandatoryRequestHeaders;

    private Class controller;
    private Method method;

    private String controllerId;
    private String methodName;
    private String methodArgs;

    public RouteBuilder(String verb, String path, Vinna context, List<ActionArgument> methodParameters) {
        this.path = path;
        this.context = context;
        this.verb = verb;
        this.methodParameters = methodParameters;

        this.mandatoryQueryParameters = new HashMap<>();
        this.mandatoryRequestHeaders = new HashMap<>();
    }

    public RouteBuilder hasHeader(String name) {
        return hasHeader(name, null);
    }

    public RouteBuilder hasHeader(String name, String pattern) {
        if (pattern != null) {
            mandatoryRequestHeaders.put(name, Pattern.compile(pattern));
        } else {
            mandatoryRequestHeaders.put(name, null);
        }
        return this;
    }

    public RouteBuilder hasParam(String name) {
        return hasParam(name, null);
    }

    public RouteBuilder hasParam(String name, String pattern) {
        if (pattern != null) {
            mandatoryQueryParameters.put(name, Pattern.compile(pattern));
        } else {
            mandatoryQueryParameters.put(name, null);
        }
        return this;
    }

    public RouteBuilder withControllerId(String controllerId) {
        if (this.controllerId != null) {
            throw new ConfigException("ControllerId already defined");
        }
        this.controllerId = controllerId;
        return this;
    }

    public void withMethod(String methodPattern) {
        Matcher methodMatcher = METHOD_PATTERN.matcher(methodPattern);
        if (methodMatcher.matches()) {
            this.methodName = methodMatcher.group("method");
            this.methodArgs = methodMatcher.group("args");

            RoutesParser.ParsedPath parsedPath = RoutesParser.parsePath(path);
            Route.Action action = new Route.Action(controllerId, methodName, RoutesParser.parseArgs(methodArgs));
            Route route = new Route(this.verb, parsedPath.pathPattern, this.mandatoryQueryParameters, parsedPath.variableNames, mandatoryRequestHeaders, action);
            context.addRoute(route);

        } else {
            throw new ConfigException("Incorrect method pattern");
        }
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
            throw new ConfigException("Can't create controller", e);
        }
        return proxy;
    }

    private Route createRoute() {
        RoutesParser.ParsedPath parsedPath = RoutesParser.parsePath(path);
        Route.Action action = new Route.Action(controllerId, controller, method, methodParameters);
        return new Route(this.verb, parsedPath.pathPattern, this.mandatoryQueryParameters, parsedPath.variableNames, mandatoryRequestHeaders, action);
    }

    private class RouteMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            // be careful with the method finalize
            if (method == null) {
                if (Outcome.class.isAssignableFrom(thisMethod.getReturnType())) {
                    if (methodParameters.size() != args.length) {
                        throw new ConfigException("Like, really ?");
                    }
                    method = thisMethod;
                    context.addRoute(createRoute());
                } else {
                    throw new ConfigException("Sorry, witchery is only available at Poudlard");
                }
            }
            return null;
        }
    }

}
