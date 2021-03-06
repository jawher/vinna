package vinna.route;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import vinna.Vinna;
import vinna.exception.ConfigException;
import vinna.response.Response;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class RouteBuilder {
    private final String verb;
    private final Vinna context;
    private final List<ActionArgument> methodParameters;

    private final Map<String, Pattern> mandatoryQueryParameters;
    private final Map<String, Pattern> mandatoryRequestHeaders;

    private Class controller;
    private Method method;
    private String controllerId;
    private final RoutesParser.ParsedPath parsedPath;

    public RouteBuilder(String verb, String path, String prefix, Vinna context, List<ActionArgument> methodParameters) {
        parsedPath = RoutesParser.parsePath(path, prefix);
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

    public void pass() {
        Route route = new Route(this.verb, parsedPath.pathPattern, parsedPath.variableNames, this.mandatoryQueryParameters, mandatoryRequestHeaders, PassAction.INSTANCE);
        context.addRoute(route);
    }

    public void withMethod(String methodPattern) {
        RouteResolution.Action action = RoutesParser.parseAction(controllerId + "." + methodPattern, context.getBasePackage(), parsedPath.variableNames);
        Route route = new Route(this.verb, parsedPath.pathPattern, parsedPath.variableNames, this.mandatoryQueryParameters, mandatoryRequestHeaders, action);
        context.addRoute(route);
    }

    public <T> T withController(Class<T> controller) {
        this.controller = controller;

        if (!controller.isInterface()) {
            // The controller is not an interface. Trying to create a proxy with javassist
            // Final class cannot be extend and we cannot intercept execute to final method

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
        RouteResolution.Action action = new InvokeMethodAction(controllerId, controller, method, methodParameters);
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
                    context.addRoute(createRoute());
                } else {
                    throw new ConfigException("Sorry, witchery is only available at Poudlard");
                }
            }
        }
    }
}
