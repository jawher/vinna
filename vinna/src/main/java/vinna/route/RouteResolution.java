package vinna.route;

import vinna.Vinna;
import vinna.outcome.Outcome;
import vinna.request.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteResolution {

    private final Map<String, String> paramValues;
    private final Route.Action action;
    private final Request request;

    public RouteResolution(Route.Action action, Map<String, String> paramValues, Request request) {
        this.paramValues = paramValues;
        this.action = action;
        this.request = request;
    }


    public Outcome callAction(Vinna vinna) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object controllerInstance = vinna.createController(action.controllerId, action.controllerClass);
        Class<?> controllerClz = controllerInstance.getClass();
        Method toCall = action.method;
        if (toCall == null) {
            toCall = selectMethod(controllerClz);
            if (toCall == null) {
                throw new IllegalArgumentException("no methodName " + action.methodName + " in " + action.controllerId);
            }
        }


        List<Object> castedParams = new ArrayList<>();
        Class[] argTypes = toCall.getParameterTypes();

        ActionArgument.Environment env = new ActionArgument.Environment(request, paramValues);
        for (int i = 0; i < argTypes.length; i++) {
            //FIXME: handle conversion errors in resolve: what to do ? 404 ?
            final Class argType = argTypes[i];
            final ActionArgument actionArgument = action.methodParameters.get(i);

            castedParams.add(actionArgument.resolve(env, argType));
        }
        // throw exception or return an ErrorOutcome ?
        return (Outcome) toCall.invoke(controllerInstance, castedParams.toArray());
    }

    private Method selectMethod(Class<?> controllerClz) {
        List<Method> matchingMethods = new ArrayList<>();
        for (Method controllerMethod : controllerClz.getDeclaredMethods()) {
            if (isSuitable(controllerMethod)) {
                matchingMethods.add(controllerMethod);
            }
        }
        if (matchingMethods.isEmpty()) {
            throw new RuntimeException(String.format("The controller %s has no methods named '%s' and taking %d param(s) of the desired types",
                    controllerClz, action.methodName, action.methodParameters.size()));
        } else if (matchingMethods.size() > 1) {
            throw new RuntimeException(String.format("Ambiguous situation: The controller %s has %d methods named '%s' and taking %d param(s)",
                    controllerClz, matchingMethods.size(), action.methodName, action.methodParameters.size()));
        } else {
            return matchingMethods.get(0);
        }
    }

    private boolean isSuitable(Method controllerMethod) {
        if (controllerMethod.getName().equals(action.methodName) && controllerMethod.getParameterTypes().length == action.methodParameters.size()) {
            List<ActionArgument> methodParameters = action.methodParameters;
            for (int i = 0, methodParametersSize = methodParameters.size(); i < methodParametersSize; i++) {
                ActionArgument argument = methodParameters.get(i);
                Class<?> targetType = controllerMethod.getParameterTypes()[i];
                if (!argument.compatibleWith(targetType)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
