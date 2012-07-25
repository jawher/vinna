package vinna.route;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import vinna.Vinna;
import vinna.outcome.Outcome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RouteBuilder {
    private final String path;
    private final String verb;
    private final Vinna context;

    private Class controller;
    private Method method;

    public RouteBuilder(String verb, String path, Vinna context) {
        this.path = path;
        this.context = context;
        this.verb = verb;
    }

    public <T> T withController(Class<T> controller) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        this.controller = controller;

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(controller);

        T proxy = (T) factory.create(new Class<?>[0], new Object[0], new RouteMethodHandler());
        return proxy;
    }

    private Route createRoute() {
        return new Route(verb, Pattern.compile(path),new HashMap<String, Pattern>(), new ArrayList<String>(), controller, method);
        // TODO
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
                    throw new Exception("Sorry, witchery is only available at Poutlard");
                }
            }
            return null;
        }
    }


}
