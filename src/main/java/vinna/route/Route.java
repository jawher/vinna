package vinna.route;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {

    // TODO multiple action per route
    static final class Action {
        public final String controllerId;
        public final Class<?> controllerClass;
        public final String methodName;
        public final Method method;
        public final List<ActionArgument> parameters;

        public Action(String controllerId, String methodName, List<ActionArgument> parameters) {
            this.controllerId = controllerId;
            this.controllerClass = null;
            this.methodName = methodName;
            this.method = null;
            this.parameters = parameters;
        }

        public Action(String controllerId, Class<?> controllerClass, Method method, List<ActionArgument> parameters) {
            this.controllerId = controllerId;
            this.controllerClass = controllerClass;
            this.methodName = null;
            this.method = method;
            this.parameters = parameters;
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
            Map<String, String> variablesNames = new HashMap<>();

            for (ActionArgument actionArgument : action.parameters) {
                switch (actionArgument.type) {
                    case VARIABLE:
                        String variableValue = m.group(actionArgument.value);
                        variablesNames.put(actionArgument.value, variableValue);

                        System.out.println("\t" + actionArgument.value + "=" + variableValue);
                        break;
                    case CONSTANT:
                        variablesNames.put(actionArgument.value, actionArgument.value);

                        System.out.println("\t" + actionArgument.value + "=" + actionArgument.value);
                        break;
                }
            }
            return new RouteResolution(action, variablesNames);
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
