package vinna.route;

import vinna.outcome.Outcome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    public class RouteResolution {
        private LinkedList<String> paramValues;

        public RouteResolution(LinkedList<String> paramValues) {
            this.paramValues = paramValues;
        }

        public Outcome callAction() throws IllegalAccessException, InstantiationException, InvocationTargetException {
            // TODO objectFactory
            Object controllerInstance = controller.newInstance();

            List<Object> castedParams = new ArrayList<>();
            for (Class clazz : method.getParameterTypes()) {
                castedParams.add(clazz.cast(paramValues.removeFirst()));
            }

            // throw exception or return an ErrorOutcome ?
            return (Outcome) method.invoke(controllerInstance, castedParams.toArray());
        }
    }

    private final String verb;
    private final Pattern pathPattern;
    private final Map<String, Pattern> args;
    private final Collection<String> variableNames;
    private final Class controller;
    private Method method;

    public Route(String verb, Pattern pathPattern, Map<String, Pattern> args, Collection<String> variableNames, Class controller, Method method) {
        this.verb = verb;
        this.pathPattern = pathPattern;
        this.args = args;
        this.variableNames = variableNames;
        this.controller = controller;
        this.method = method;
    }

    public Route(String verb, Pattern pathPattern, Map<String, Pattern> args, Collection<String> variableNames, String action) throws ClassNotFoundException {
        this.verb = verb;
        this.pathPattern = pathPattern;
        this.args = args;
        this.variableNames = variableNames;

        // TODO
        int separatorPos = action.lastIndexOf('.');

        String className = action.substring(0, separatorPos);
        String methodName = action.substring(separatorPos + 1, action.indexOf('('));
        System.err.println("class:" + className);
        System.err.println("method:" + methodName);
        controller = Class.forName(className);

        for (Method controllerMethod : controller.getDeclaredMethods()) {
            if (controllerMethod.getName().equals(methodName)) {
                method = controllerMethod;
                break;
            }
        }
    }

    public RouteResolution match(String path) {
        Matcher m = pathPattern.matcher(path);
        if (m.matches()) {
            System.out.println("Got match for " + toString());
            for (String variableName : variableNames) {
                // TODO LinkedList
                System.out.println("\t" + variableName + "=" + m.group(variableName));
            }
            return new RouteResolution(new LinkedList<String>());
        }
        return null;
    }

    public boolean hasVerb(String verb) {
        return this.verb.equalsIgnoreCase(verb);
    }

    @Override
    public String toString() {
        return "Route{" + verb + " " + pathPattern + " " + method + " }";
        //return "Route{" + verb + " " + pathPattern + "?" + args + " => ";
    }
}
