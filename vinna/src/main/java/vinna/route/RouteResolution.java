package vinna.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.Vinna;
import vinna.exception.ConversionException;
import vinna.exception.VuntimeException;
import vinna.http.Request;
import vinna.response.Response;
import vinna.response.ResponseBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteResolution {
    private final static Logger logger = LoggerFactory.getLogger(RouteResolution.class);

    private final Map<String, String> paramValues;
    private final Route.Action action;
    private final Request request;

    public RouteResolution(Route.Action action, Map<String, String> paramValues, Request request) {
        this.paramValues = paramValues;
        this.action = action;
        this.request = request;
    }

    public Response callAction(Vinna vinna) {
        String controllerId = action.controllerId;
        if (controllerId != null) {
            controllerId = evaluate(controllerId, paramValues);
        }
        String methodName = action.methodName;
        if (methodName != null) {
            methodName = evaluate(methodName, paramValues);
        }
        Object controllerInstance = vinna.createController(controllerId, action.controllerClass);
        Class<?> controllerClz = controllerInstance.getClass();
        Method toCall = action.method;
        if (toCall == null) {
            toCall = selectMethod(controllerClz, methodName);
            if (toCall == null) {
                throw new VuntimeException("no methodName " + action.methodName + " in " + action.controllerId);
            }
        }

        List<Object> castedParams = new ArrayList<>();
        Class[] argTypes = toCall.getParameterTypes();

        try {
            ActionArgument.Environment env = new ActionArgument.Environment(request, paramValues);
            for (int i = 0; i < argTypes.length; i++) {

                final Class argType = argTypes[i];
                final ActionArgument actionArgument = action.methodParameters.get(i);

                castedParams.add(actionArgument.resolve(env, argType));
            }

            return (Response) toCall.invoke(controllerInstance, castedParams.toArray());
        } catch (ConversionException e) {
            //FIXME: handle conversion errors in resolve: what to do ? 404 ?
            return ResponseBuilder.withStatus(500);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new VuntimeException(e);
        }
    }

    private String evaluate(String s, Map<String, String> paramValues) {
        StringBuffer res = new StringBuffer();
        Matcher m = Pattern.compile("\\{(.+?)\\}").matcher(s);
        while (m.find()) {
            String key = m.group(1);
            if (!paramValues.containsKey(key)) {
                throw new VuntimeException("Unknown variable " + key);
            }
            m.appendReplacement(res, paramValues.get(key));
        }
        m.appendTail(res);
        return res.toString();
    }

    private Method selectMethod(Class<?> controllerClz, String methodName) {
        List<Method> matchingMethods = new ArrayList<>();
        for (Method controllerMethod : controllerClz.getDeclaredMethods()) {
            if (isSuitable(controllerMethod, methodName)) {
                matchingMethods.add(controllerMethod);
            }
        }
        if (matchingMethods.isEmpty()) {
            throw new VuntimeException(String.format("The controller %s has no methods named '%s' and taking %d param(s) of the desired types",
                    controllerClz, action.methodName, action.methodParameters.size()));
        } else if (matchingMethods.size() > 1) {
            throw new VuntimeException(String.format("Ambiguous situation: The controller %s has %d methods named '%s' and taking %d param(s)",
                    controllerClz, matchingMethods.size(), action.methodName, action.methodParameters.size()));
        } else {
            return matchingMethods.get(0);
        }
    }

    private boolean isSuitable(Method controllerMethod, String methodName) {
        if (controllerMethod.getName().equals(methodName) && controllerMethod.getParameterTypes().length == action.methodParameters.size()) {
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
