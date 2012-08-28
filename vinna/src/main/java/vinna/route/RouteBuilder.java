package vinna.route;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import vinna.Vinna;
import vinna.exception.ConfigException;
import vinna.response.Response;

import java.lang.reflect.*;
import java.util.*;
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

    private Collection<ActionArgument.RequestParameter> getPrimitiveTypeRequestParameter() {
        ArrayList<ActionArgument.RequestParameter> parameters = new ArrayList<>();
        for (ActionArgument parameter : methodParameters) {
            if (parameter instanceof ActionArgument.RequestParameter) {
                ActionArgument.RequestParameter requestParameter = (ActionArgument.RequestParameter) parameter;
                /**
                 * I can't use requestParameter.getClass().isPrimitive() since it accepts Char, Byte and Void.
                 * They aren't supported yet.
                 */
                if (requestParameter.compatibleWith(Long.class) ||
                        requestParameter.compatibleWith(Integer.class) ||
                        requestParameter.compatibleWith(Short.class) ||
                        requestParameter.compatibleWith(Byte.class) ||
                        requestParameter.compatibleWith(Float.class) ||
                        requestParameter.compatibleWith(Double.class) ||
                        requestParameter.compatibleWith(Boolean.class)) {
                    parameters.add(requestParameter);
                }
            }
        }
        return parameters;
    }

    private void checkUsedRequestParametersAreMandatory() {
        for (ActionArgument.RequestParameter requestParameter : getPrimitiveTypeRequestParameter()) {
            if (!mandatoryQueryParameters.containsKey(requestParameter.getName())) {
                String message = String.format("Query param '%s' is not defined as mandatory, please use #hasParam",
                        requestParameter.getName());
                throw new ConfigException(message);
            }
        }
    }

    public void withMethod(String methodPattern) {
        Matcher methodMatcher = METHOD_PATTERN.matcher(methodPattern);
        if (methodMatcher.matches()) {
            String methodName = methodMatcher.group("method");
            String methodArgs = methodMatcher.group("args");

            RoutesParser.ParsedPath parsedPath = RoutesParser.parsePath(path);
            Route.Action action = new Route.Action(controllerId, methodName, RoutesParser.parseArgs(methodArgs));
            Route route = new Route(this.verb, parsedPath.pathPattern, parsedPath.variableNames, this.mandatoryQueryParameters, mandatoryRequestHeaders, action);
            context.addRoute(route);

        } else {
            throw new ConfigException("Incorrect method pattern");
        }
    }

    public <T> T withController(Class<T> controller) {
        this.controller = controller;

        Class<?>[] interfaces = controller.getInterfaces();
        if (!controller.isInterface()) {
            // The controller is not an interface. Trying to create a proxy with javassist
            // Final class cannot be extend and we cannot intercept call to final method

            if (Modifier.isFinal(controller.getModifiers())) {
                throw new ConfigException("Cannot create proxy of final controller");
            }

            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(controller);
            factory.setFilter(new MethodFilter() {
                @Override
                public boolean isHandled(Method m) {
                    // ignore finalize()
                    return !m.getName().equals("finalize");
                }
            });

            // TODO constructor with params
            T proxy = null;
            try {
                proxy = (T) factory.create(new Class<?>[0], new Object[0], new RouteMethodHandler());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ConfigException("Can't create controller", e);
            }
            return proxy;
        }

        // trying to create a proxy with JDK dynamic proxy
        Object obj = Proxy.newProxyInstance(controller.getClassLoader(), new Class<?>[]{controller}, new RouteMethodHandler());
        return (T) obj;
    }

    private Route createRoute() {
        RoutesParser.ParsedPath parsedPath = RoutesParser.parsePath(path);
        Route.Action action = new Route.Action(controllerId, controller, method, methodParameters);
        return new Route(this.verb, parsedPath.pathPattern, parsedPath.variableNames, this.mandatoryQueryParameters, mandatoryRequestHeaders, action);
    }

    private class RouteMethodHandler implements MethodHandler, InvocationHandler {

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            methodHandler(thisMethod, args);
            return null;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            methodHandler(method, args);
            return null;
        }

        private void methodHandler(Method thisMethod, Object[] args) {
            int argsSize = 0;
            if (args != null) {
                argsSize = args.length;
            }

            // be careful with the method finalize
            if (method == null) {
                if (Response.class.isAssignableFrom(thisMethod.getReturnType())) {
                    if (methodParameters.size() != argsSize) {
                        throw new ConfigException("Like, really ?");
                    }
                    method = thisMethod;
                    checkUsedRequestParametersAreMandatory();
                    context.addRoute(createRoute());
                } else {
                    throw new ConfigException("Sorry, witchery is only available at Poudlard");
                }
            }
        }
    }

}
