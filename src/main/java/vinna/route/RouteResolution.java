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
    private final Vinna vinna;

    public RouteResolution(Route.Action action, Map<String, String> paramValues, Request request, Vinna vinna) {
        this.paramValues = paramValues;
        this.action = action;
        this.request = request;
        this.vinna = vinna;
    }

    public Outcome callAction() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object controllerInstance = vinna.createController(action.controllerId, action.controllerClass);
        Class<?> controllerClz = controllerInstance.getClass();
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


        List<Object> castedParams = new ArrayList<>();
        Class[] argTypes = toCall.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            //FIXME: handle conversion errors in resolve: what to do ? 404 ?
            castedParams.add(action.parameters.get(i).resolve(new ActionArgument.Environment(request, paramValues, argTypes[i])));
        }
        // throw exception or return an ErrorOutcome ?
        return (Outcome) toCall.invoke(controllerInstance, castedParams.toArray());
    }
}
