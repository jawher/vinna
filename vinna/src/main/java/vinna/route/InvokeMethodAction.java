package vinna.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.ConversionException;
import vinna.exception.VuntimeException;
import vinna.response.Response;
import vinna.response.ResponseBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvokeMethodAction implements RouteResolution.Action {
    private static final Logger log = LoggerFactory.getLogger(InvokeMethodAction.class);
    private static final Pattern EVALUATE_PATTERN = Pattern.compile("\\{(.+?)\\}");

    public final String controllerId;
    public final Class<?> controllerClass;
    public final String methodName;
    public final Method method;
    public final List<ActionArgument> methodParameters;

    public InvokeMethodAction(String controllerId, String methodName, List<ActionArgument> methodParameters) {
        this.controllerId = controllerId;
        this.controllerClass = null;
        this.methodName = methodName;
        this.method = null;
        this.methodParameters = methodParameters;
    }

    public InvokeMethodAction(String controllerId, Class<?> controllerClass, Method method, List<ActionArgument> methodParameters) {
        this.controllerId = controllerId;
        this.controllerClass = controllerClass;
        this.methodName = null;
        this.method = method;
        this.methodParameters = methodParameters;
    }

    @Override
    public String toString() {
        return (controllerClass == null ? controllerId : controllerClass.getName()) + "." + (method == null ? methodName : method.getName());
    }

    @Override
    public Response execute(Environment environment) {
        String controllerId = this.controllerId;
        if (controllerId != null) {
            controllerId = evaluate(controllerId, environment.matchedVars);
        }
        String methodName = this.methodName;
        if (methodName != null) {
            methodName = evaluate(methodName, environment.matchedVars);
        }
        Object controllerInstance = environment.vinna.getControllerFactory().create(controllerId, controllerClass);
        Class<?> controllerClz = controllerInstance.getClass();
        Method toCall = method;
        if (toCall == null) {
            toCall = selectMethod(controllerClz, methodName);
            if (toCall == null) {
                throw new VuntimeException("no methodName " + methodName + " in " + controllerId);
            }
        }

        List<Object> castedParams = new ArrayList<>();
        Class[] argTypes = toCall.getParameterTypes();

        try {

            for (int i = 0; i < argTypes.length; i++) {

                final Class argType = argTypes[i];
                final ActionArgument actionArgument = methodParameters.get(i);

                try {
                    castedParams.add(actionArgument.resolve(environment, argType));
                } catch (ConversionException e) {
                    //FIXME: handle conversion errors in resolve: what to do ? 404 ?
                    log.error("Error while converting argument " + actionArgument + " to type " + argType, e);
                    return ResponseBuilder.withStatus(500);
                }
            }

            return (Response) toCall.invoke(controllerInstance, castedParams.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new VuntimeException(e);
        }
    }

    private String evaluate(String s, Map<String, String> paramValues) {
        StringBuffer res = new StringBuffer();
        Matcher m = EVALUATE_PATTERN.matcher(s);
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
                    controllerClz, methodName, methodParameters.size()));
        } else if (matchingMethods.size() > 1) {
            throw new VuntimeException(String.format("Ambiguous situation: The controller %s has %d methods named '%s' and taking %d param(s)",
                    controllerClz, matchingMethods.size(), methodName, methodParameters.size()));
        } else {
            return matchingMethods.get(0);
        }
    }

    private boolean isSuitable(Method controllerMethod, String methodName) {
        if (controllerMethod.getName().equals(methodName) && controllerMethod.getParameterTypes().length == methodParameters.size()) {
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
