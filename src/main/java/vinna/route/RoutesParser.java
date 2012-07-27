package vinna.route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoutesParser {

    public final List<Route> loadFrom(Reader reader) {
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
                        routes.add(new Route(verb, parsedPath.pathPattern, parsedPath.queryMap, parsedPath.variableNames, parsedAction.controller, parsedAction.method));


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

        public ParsedAction(String controller, String method) {
            this.controller = controller;
            this.method = method;
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
                return new ParsedAction(m.group("controller"), m.group("method"));
            } else {
                throw new RuntimeException("Invalid class and method " + controllerAndMethod);
            }


        } else {
            throw new RuntimeException("Invalid action");
        }
    }

}
