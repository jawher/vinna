package vinna.route;

import vinna.outcome.Outcome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteResolution {
    private final Map<String, String> paramValues;
    private final Route.Action action;

    public RouteResolution(Route.Action action, Map<String, String> paramValues) {
        this.paramValues = paramValues;
        this.action = action;
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
        Class[] argTypes = toCall.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            castedParams.add(action.parameters.get(i).resolve(paramValues, argTypes[i]));
            System.err.println(argTypes[i]);

        }
        System.err.println(castedParams);
        // throw exception or return an ErrorOutcome ?
        return (Outcome) toCall.invoke(controllerInstance, castedParams.toArray());
    }


}
