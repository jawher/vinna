package vinna.route;

import vinna.Vinna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoutesParser {

    public final List<Route> loadFrom(Reader reader, Vinna vinna) {
        List<Route> routes = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        int lineNum = 0;
        Pattern routeLine = Pattern.compile("(?<verb>.*?)\\s+(?<path>.*?)\\s+(?<action>.*?)");
        try {
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    Matcher rm = routeLine.matcher(line);
                    if (!rm.matches()) {
                        throw new RuntimeException("Invalid syntax in routes file (line " + lineNum + ")\n" + line);
                    } else {
                        // TODO method for creating the pathPattern, the queryMap and variablesNames. Needed by RouteBuilder
                        String verb = rm.group("verb");
                        String path = rm.group("path");
                        String action = rm.group("action");


                        ParsedPath parsedPath = parsePath(path);
                        ParsedAction parsedAction = parseAction(action);
                        routes.add(new Route(verb, parsedPath.pathPattern, parsedPath.queryMap, parsedPath.variableNames,
                                new Route.Action(parsedAction.controller, parsedAction.method, parsedAction.parameters), vinna));


                        //log how vitta sees this route, as matcher.find is too forgiving
                    }
                }
            }
            return routes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static final class ParsedPath {
        public final Pattern pathPattern;
        public final Map<String, Pattern> queryMap;
        public final Collection<String> variableNames;

        public ParsedPath(Pattern pathPattern, Map<String, Pattern> queryMap, Collection<String> variableNames) {
            this.pathPattern = pathPattern;
            this.queryMap = queryMap;
            this.variableNames = variableNames;
        }
    }

    public static ParsedPath parsePath(String path) {
        String variable = "\\{(?<name>.+?)(\\s*:\\s*(?<pattern>.+?))?\\}";
        Pattern pathSegmentPattern = Pattern.compile("(?<ls>/)(" + variable + "|(?<seg>[^/?]+))");

        List<String> variablesNames = new ArrayList<>();
        StringBuilder pathPattern = new StringBuilder();
        if (!path.startsWith("/")) {
            pathPattern.append(".*?");
            path = "/" + path;
        }
        Matcher m = pathSegmentPattern.matcher(path);
        int lastEnd = 0;
        while (m.find()) {
            lastEnd = m.toMatchResult().end();
            pathPattern.append("/");
            if (m.group("seg") != null) {
                pathPattern.append(m.group("seg"));
            } else {
                String pattern = m.group("pattern");
                String name = m.group("name");
                variablesNames.add(name);
                if (pattern != null) {
                    pathPattern.append("(?<").append(name).append(">").append(pattern).append(")");
                } else {
                    pathPattern.append("(?<").append(name).append(">").append("[^/]+").append(")");
                }
            }
        }

        //query string
        String query = path.substring(lastEnd);
        if (query.startsWith("/")) {
            pathPattern.append("/");
            query = query.substring(1);
        }
        if (query.startsWith("?")) {
            query = "&" + query.substring(1);
        }
        Map<String, Pattern> queryMap = new HashMap<>();
        if (!query.isEmpty()) {
            Pattern queryVar = Pattern.compile("&" + variable);
            m = queryVar.matcher(query);
            while (m.find()) {
                String pattern = m.group("pattern");
                String name = m.group("name");
                variablesNames.add(name);
                if (pattern != null) {
                    queryMap.put(name, Pattern.compile(pattern));
                } else {
                    queryMap.put(name, null);
                }
            }
        }

        System.out.println(pathPattern);
        System.out.println(queryMap);
        return new ParsedPath(Pattern.compile(pathPattern.toString()), queryMap, variablesNames);
    }

    public static final class ParsedAction {
        public final String controller;
        public final String method;
        public final List<ActionArgument> parameters;

        public ParsedAction(String controller, String method, List<ActionArgument> parameters) {
            this.controller = controller;
            this.method = method;
            this.parameters = parameters;
        }
    }

    public static ParsedAction parseAction(String action) {
        /*
        controller.method({arg}, "string", true, 4, 3.6, {})
        controller.method()
        package.controller.method()
         */

        Pattern actionPattern = Pattern.compile("(?<controllerAndMethod>.+())\\((?<args>.*)\\)");
        //String action = "pkg.controller.method({arg}, \"string\", true, 4, 3.6, {})";
        Matcher actionMatcher = actionPattern.matcher(action);
        if (actionMatcher.matches()) {
            String controllerAndMethod = actionMatcher.group("controllerAndMethod");

            Matcher m = Pattern.compile("(?<controller>.+)\\.(?<method>[^\\.]+)$").matcher(controllerAndMethod);
            if (m.matches()) {
                String controller = m.group("controller");
                String method = m.group("method");

                String argsString = actionMatcher.group("args").trim();
                List<ActionArgument> parameters = new ArrayList<>();
                Pattern pvar = Pattern.compile("\\{(.+)\\}");
                Pattern pstr = Pattern.compile("\"((\\.|.)*)\"");
                Pattern pbool = Pattern.compile("(true|false)");
                if (!argsString.isEmpty()) {
                    String[] args = argsString.split("\\s*,\\s*");
                    for (String arg : args) {
                        Matcher pm;
                        if ((pm = pvar.matcher(arg)).matches()) {
                            parameters.add(new ActionArgument.Variable(pm.group(1)));
                        } else if ((pm = pstr.matcher(arg)).matches()) {
                            parameters.add(new ActionArgument.Const<String>(pm.group(1)));
                        } else if ((pm = pbool.matcher(arg)).matches()) {
                            parameters.add(new ActionArgument.Const<Boolean>(Boolean.parseBoolean(pm.group(1))));
                        } else {
                            try {
                                parameters.add(new NumConst(new BigDecimal(arg)));
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Invalid action argument " + arg);
                            }
                        }
                    }
                }


                return new ParsedAction(controller, method, parameters);
            } else {
                throw new RuntimeException("Invalid class and method " + controllerAndMethod);
            }


        } else {
            throw new RuntimeException("Invalid action");
        }
    }

    private static final class NumConst extends ActionArgument {
        private final BigDecimal value;

        public NumConst(BigDecimal value) {
            this.value = value;
        }

        @Override
        protected Object resolve(Environment env) {
            if (Long.class.equals(env.targetType) || Long.TYPE.equals(env.targetType)) {
                return value.longValue();
            } else if (Integer.class.equals(env.targetType) || Integer.TYPE.equals(env.targetType)) {
                return value.intValue();
            } else if (Short.class.equals(env.targetType) || Short.TYPE.equals(env.targetType)) {
                return value.intValue();
            } else if (Byte.class.equals(env.targetType) || Byte.TYPE.equals(env.targetType)) {
                return value.byteValue();
            } else if (Double.class.equals(env.targetType) || Double.TYPE.equals(env.targetType)) {
                return value.doubleValue();
            } else if (Float.class.equals(env.targetType) || Float.TYPE.equals(env.targetType)) {
                return value.floatValue();
            } else if (BigDecimal.class.equals(env.targetType)) {
                return value;
            } else if (BigInteger.class.equals(env.targetType)) {
                return value.toBigInteger();
            }
            throw new IllegalArgumentException("Cannot convert a numeric value to " +env.targetType);
        }
    }

}
