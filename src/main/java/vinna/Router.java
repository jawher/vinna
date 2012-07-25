package vinna;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {
    public static class Route {
        public final String verb;
        public final Pattern pathPattern;
        public final Map<String, Pattern> args;
        public final Collection<String> variableNames;
        public final String action;
        //TODO: add action

        public Route(String verb, Pattern pathPattern, Map<String, Pattern> args, Collection<String> variableNames, String action) {
            this.verb = verb;
            this.pathPattern = pathPattern;
            this.args = args;
            this.variableNames = variableNames;
            this.action = action;
        }

        @Override
        public String toString() {
            return "Route{" + verb + " " + pathPattern + "?" + args + " => " + action;
        }
    }

    private final List<Route> routes = new ArrayList<>();

    public final void addRoute(Route route) {
        routes.add(route);
    }

    public final void loadFrom(Reader reader) {
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
                        String verb = rm.group("verb");
                        String path = rm.group("path");
                        String action = rm.group("action");

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

                        routes.add(new Route(verb, Pattern.compile(pathPattern.toString()), queryMap, variablesNames, action));

                        //log how vitta sees this route, as matcher.find is too forgiving
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Route match(HttpServletRequest r) {
        for (Router.Route route : routes) {
            System.out.println("checking against " + route);
            if (r.getMethod().equalsIgnoreCase(route.verb)) {
                Matcher m = route.pathPattern.matcher(r.getServletPath());
                if (m.matches()) {
                    System.out.println("Got match for " + route.action);
                    for (String variableName : route.variableNames) {
                        System.out.println("\t" + variableName + "=" + m.group(variableName));
                    }
                    return route;
                }
            }
        }
        return null;
    }


    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

}
