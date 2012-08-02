package vinna.route;

import vinna.request.Request;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {

    // TODO multiple action per route
    public static final class Action {
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


    public RouteResolution match(Request request) {
        if (request.getVerb().equalsIgnoreCase(verb)) {

            Matcher m = pathPattern.matcher(request.getPath());
            if (m.matches()) {
                System.out.println("Got match for " + toString());
                Map<String, String> paramValues = new HashMap<>();

                for (String variablesName : variableNames) {
                    //FIXME: check that the variable exists, or else that it is optional
                    if (args.containsKey(variablesName)) {
                        if (!request.getParams(variablesName).isEmpty()) {
                            String param = request.getParams(variablesName).iterator().next();
                            if (args.get(variablesName) != null) {
                                if (!args.get(variablesName).matcher(param).matches()) {
                                    return null;
                                } else {
                                    paramValues.put(variablesName, param);
                                }
                            } else { //no pattern, put it as it is
                                paramValues.put(variablesName, param);
                            }
                        }
                    } else {
                        paramValues.put(variablesName, m.group(variablesName));
                    }
                }
                return new RouteResolution(action, paramValues, request);
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "Route{" + verb + " " + pathPattern + " " + action + " }";
    }
}
