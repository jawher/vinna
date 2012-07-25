package vinna;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws UnsupportedEncodingException {
        //parsePath();
        new Router().loadFrom(new InputStreamReader(App.class.getResourceAsStream("sample.routes"), "utf-8"));
        //parseAction();
    }

    private static void parseAction() {
        /*
        controller.method({arg}, "string", true, 4, 3.6, {})
        controller.method()
        package.controller.method()
         */

        Pattern actionPattern = Pattern.compile("(?<clzMeth>.+())\\((?<args>.*)\\)");
        String action = "pkg.controller.method({arg}, \"string\", true, 4, 3.6, {})";
        Matcher actionMatcher = actionPattern.matcher(action);
        if(actionMatcher.matches()) {
            String clzMeth = actionMatcher.group("clzMeth");

            Matcher m = Pattern.compile("(.+)\\.([^\\.]+)$").matcher(clzMeth);
            if(m.matches()) {
                System.out.println(m.group(1)+" :<>: "+m.group(2));
            } else {
                throw new RuntimeException("Invalid class and method "+clzMeth);
            }


            String args = actionMatcher.group("args");
            System.out.println(clzMeth+" :: "+args);
        } else {
            throw new RuntimeException("Invalid action");
        }
    }

    private static void parsePath() {
        /*
         /users/
         users/
         /users

        */

        String path = "/users/{<\\d+>id}?{x}&{<true|false>debug}";
        String variable = "\\{(<(?<pattern>.+?)>)?(?<name>.+?)\\}";
        Pattern pathSegmentPattern = Pattern.compile("(?<ls>/)(" + variable + "|(?<seg>[^/?]+))");

        StringBuilder pathPattern = new StringBuilder();
        System.out.println("0. " + path);
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
                if (pattern != null) {
                    pathPattern.append("(?<").append(name).append(">").append(pattern).append(")");
                } else {
                    pathPattern.append(name);
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
        System.out.println("?=" + query);
        Map<String, String> queryMap = new HashMap<>();
        if (!query.isEmpty()) {
            Pattern queryVar = Pattern.compile("&" + variable);
            m = queryVar.matcher(query);
            while (m.find()) {
                String pattern = m.group("pattern");
                String name = m.group("name");
                if (pattern != null) {
                    queryMap.put(name, pattern);
                } else {
                    queryMap.put(name, ".*");
                }
            }
        }
        System.out.println(":" + path);

        System.out.println(pathPattern);
        System.out.println(queryMap);

        //log how vitta sees this route, as matcher.find is too forgiving
    }
}
