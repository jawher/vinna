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
        public final List<ActionArgument> methodParameters;

        public Action(String controllerId, String methodName, List<ActionArgument> methodParameters) {
            this.controllerId = controllerId;
            this.controllerClass = null;
            this.methodName = methodName;
            this.method = null;
            this.methodParameters = methodParameters;
        }

        public Action(String controllerId, Class<?> controllerClass, Method method, List<ActionArgument> methodParameters) {
            this.controllerId = controllerId;
            this.controllerClass = controllerClass;
            this.methodName = null;
            this.method = method;
            this.methodParameters = methodParameters;
            // TODO check that methodParameters.size() == method.getParameterTypes().length
        }

        @Override
        public String toString() {
            return controllerId + "." + (method == null ? methodName : method.getName());
        }
    }

    private final String verb;
    private final Pattern pathPattern;
    private final Collection<String> mandatoryPathParameters;
    private final Collection<String> pathVariableName;
    private final Action action;

    public Route(String verb, Pattern pathPattern, Collection<String> mandatoryPathParameters, Collection<String> pathVariableName, Action action) {
        this.verb = verb;
        this.pathPattern = pathPattern;
        this.mandatoryPathParameters = mandatoryPathParameters;
        this.pathVariableName = pathVariableName;
        this.action = action;
    }


    public RouteResolution match(Request request) {
        if (request.getVerb().equalsIgnoreCase(verb)) {

            Matcher m = pathPattern.matcher(request.getPath());
            if (m.matches()) {
                System.out.println("Got match for " + toString());
                Map<String, String> paramValues = new HashMap<>();

                for (String variablesName : pathVariableName) {
                    //FIXME: check that the variable exists, or else that it is optional
                    paramValues.put(variablesName, m.group(variablesName));
                }
                for (String mandatoryPathParam : mandatoryPathParameters) {
                    if (request.getParams(mandatoryPathParam).isEmpty()) {
                        // TODO return bad request or check others routes
                        return null;
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
