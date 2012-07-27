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
            Class<?> controllerClz = action.controllerClass;
            if (controllerClz == null) {
                try {
                    controllerClz = Class.forName(action.controllerId);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Invalid controllerId class " + action.controllerId);
                }
            }
            Method toCall = action.method;
            if (toCall == null) {
                for (Method controllerMethod : controllerClz.getDeclaredMethods()) {
                    if (controllerMethod.getName().equals(action.methodName)) {
                        toCall = controllerMethod;
                        break;
                    }
                }
                if (toCall == null) {
                    throw new IllegalArgumentException("no methodName " + action.methodName + " in " + action.controllerId);
                }
            }


            Object controllerInstance = controllerClz.newInstance();

            List<Object> castedParams = new ArrayList<>();
            for (Class clazz : toCall.getParameterTypes()) {
                castedParams.add(clazz.cast(paramValues.removeFirst()));
            }

            // throw exception or return an ErrorOutcome ?
            return (Outcome) toCall.invoke(controllerInstance, castedParams.toArray());
        }
    }

    public static final class Action {
        public final String controllerId;
        public final Class<?> controllerClass;
        public final String methodName;
        public final Method method;

        public Action(String controllerId, String methodName) {
            this.controllerId = controllerId;
            this.controllerClass = null;
            this.methodName = methodName;
            this.method = null;
        }

        public Action(String controllerId, Class<?> controllerClass, Method method) {
            this.controllerId = controllerId;
            this.controllerClass = controllerClass;
            this.methodName = null;
            this.method = method;
        }

        @Override
        public String toString() {
            return controllerId + "." + (method == null ? methodName : method.getName());
        }
    }

    private final String verb;
    private final Pattern pathPattern;
    private final Map<String, Pattern> args;
    private final Collection<String> variableNames;
    private final Action action;

    public Route(String verb, Pattern pathPattern, Map<String, Pattern> args, Collection<String> variableNames, Action action) {
        this.verb = verb;
        this.pathPattern = pathPattern;
        this.args = args;
        this.variableNames = variableNames;
        this.action = action;
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
        return "Route{" + verb + " " + pathPattern + " " + action + " }";
        //return "Route{" + verb + " " + pathPattern + "?" + queryMap + " => ";
    }
}
