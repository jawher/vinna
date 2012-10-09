package vinna.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.ConfigException;
import vinna.util.Conversions;
import vinna.util.ReflectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RoutesParser {
    private static final Logger logger = LoggerFactory.getLogger(RoutesParser.class);

    public static final class ParsedPath {
        public final Pattern pathPattern;
        public final Collection<String> variableNames;

        public ParsedPath(Pattern pathPattern, Collection<String> variableNames) {
            this.pathPattern = pathPattern;
            this.variableNames = variableNames;
        }
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

    public static class ParseException extends ConfigException {
        private final String message;
        private final String line;
        private int row, col;

        public ParseException(String message, String line, int row, int col) {
            super(message);
            this.message = message;
            this.line = line;
            this.row = row;
            this.col = col;
        }

        public ParseException(String message, String line, int row, int col, Exception e) {
            super(message, e);
            this.message = message;
            this.line = line;
            this.row = row;
            this.col = col;
        }

        @Override
        public String getMessage() {
            StringBuilder res = new StringBuilder(message);
            res.append(" @ ").append(row).append(":").append(col);
            if (line != null) {
                res.append("\n").append(line).append("\n");
                for (int i = 0; i < col; i++) {
                    res.append(" ");
                }
                res.append("^");
            }
            return res.toString();
        }


    }


    private static final class NumConst implements ActionArgument {
        private final BigDecimal value;

        public NumConst(BigDecimal value) {
            this.value = value;
        }

        @Override
        public Object resolve(RouteResolution.Action.Environment env, Class<?> targetType) {
            // Could be optimized if need be: instead of doing a conversion at every invocation,
            // store the result in a fieldand return that instead. Beware of concurrency though
            // while checking the cached value and converting it
            return Conversions.convertNumeric(value, targetType);
        }

        @Override
        public boolean compatibleWith(Class<?> type) {
            return Number.class.isAssignableFrom(type);
        }
    }

    private static final class Source {
        private final BufferedReader reader;
        private int row, col;
        private String line;
        private boolean eof;

        private Source(Reader reader) {
            this.reader = new BufferedReader(reader);
            nextLine();
        }

        public boolean eol() {
            return line == null || col >= line.length();
        }

        public boolean eof() {
            return eof;
        }

        public String currentLine() {
            return line;
        }

        public int row() {
            return row;
        }

        public int col() {
            return col;
        }

        public char peek() {
            return line.charAt(col);
        }

        public char pop() {
            return line.charAt(col++);
        }

        public void nextLine() {
            if (eof) {
                return;
            }
            try {
                line = reader.readLine();
                row++;
                col = 0;
                if (line == null) {
                    eof = true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            try {
                reader.close();
            } catch (IOException e) {
                //warning
            }
        }
    }

    private Source source;

    public RoutesParser(Reader reader) {
        this.source = new Source(reader);
    }

    public static List<Route> parse(Reader routes, String prefix, String basePackage) {
        RoutesParser parser = new RoutesParser(routes);
        return parser.load(prefix, basePackage);
    }

    public static RouteResolution.Action parseAction(String action, String basePackage, Collection<String> pathVariableNames) {
        RoutesParser parser = new RoutesParser(new StringReader(action));
        return parser.readAction(basePackage, pathVariableNames);
    }

    public static RoutesParser.ParsedPath parsePath(String path, String prefix) {
        RoutesParser parser = new RoutesParser(new StringReader(path));
        return parser.parsePath(prefix);
    }

    private boolean ignoreLine(String line) {
        String l = line.trim();
        return l.isEmpty() || l.startsWith("#");
    }

    private boolean found(char c) {
        if (source.peek() == c) {
            source.pop();
            return true;
        } else {
            return false;
        }
    }

    private void expect(char c) {
        if (!found(c)) {
            if (source.eol()) {
                throw new ParseException("Premature end of line, was expecting '" + c + "'", source.currentLine(), source.row(), source.col());
            } else {
                throw new ParseException("Unexpected input, was expecting '" + c + "'", source.currentLine(), source.row(), source.col());
            }
        }
    }

    public List<Route> load(String prefix, String basePackage) {
        List<Route> routes = new ArrayList<>();
        try {
            String verb = null;
            RoutesParser.ParsedPath path = null;
            RouteResolution.Action action = null;
            Map<String, Pattern> queryVars = new HashMap<>();
            Map<String, Pattern> headers = new HashMap<>();

            while (!source.eof()) {
                if (!ignoreLine(source.currentLine())) {
                    if (Character.isWhitespace(source.peek())) {
                        readConstraint(queryVars, headers);
                    } else {
                        if (verb != null) {
                            Route route = new Route(verb, path.pathPattern, path.variableNames, queryVars, headers, action);
                            logger.debug("Route created: {}", route);
                            routes.add(route);
                            verb = null;
                            path = null;
                            action = null;
                            queryVars.clear();
                            headers.clear();
                        }
                        verb = readVerb();
                        path = parsePath(prefix);
                        action = readAction(basePackage, path.variableNames);
                    }


                }
                source.nextLine();
            }
            if (verb != null) {
                Route route = new Route(verb, path.pathPattern, path.variableNames, queryVars, headers, action);
                logger.debug("Route created: {}", route);
                routes.add(route);
            }
        } finally {
            source.close();
        }
        return routes;
    }

    void readConstraint(Map<String, Pattern> queryVars, Map<String, Pattern> headers) {
        eatWhitespace(source);
        int startPos = source.col();
        StringBuilder text = new StringBuilder();
        while (!source.eol()) {
            char c = source.peek();
            if (Character.isWhitespace(c) || c == ':') {
                break;
            }
            source.pop();
            text.append(c);
        }


        String name = text.toString().trim();
        if (name.isEmpty()) {
            throw new ParseException("Missing name for the constraint", source.currentLine(), source.row(), source.col());
        }

        Pattern pattern = null;
        eatWhitespace(source);
        if (!source.eol()) {
            if (source.peek() == ':') {
                source.pop();
                text = new StringBuilder();
                int patternStartPos = source.col();
                while (!source.eol()) {
                    text.append(source.pop());
                }
                String p = text.toString().trim();
                if (p.isEmpty()) {
                    throw new ParseException("Missing pattern for the constraint", source.currentLine(), source.row(), patternStartPos);
                }

                try {
                    pattern = Pattern.compile(p);
                } catch (PatternSyntaxException e) {
                    throw new ParseException("Invalid regex for the constraint", source.currentLine(), source.row(), patternStartPos, e);
                }
            } else {
                throw new ParseException("Unexpected input", source.currentLine(), source.row(), source.col());
            }
        }

        String s;
        if ((s = checkAndRemovePrefix(name, "req.param.")) != null) {
            queryVars.put(s, pattern);
        } else if ((s = checkAndRemovePrefix(name, "req.header.")) != null) {
            headers.put(s, pattern);
        } else {
            throw new ParseException("Unknown constraint target '" + name + "': was expecting one of req.param.<x> or req.header.<x>", source.currentLine(), source.row(), startPos);
        }

    }


    String readVerb() {
        StringBuilder text = new StringBuilder();
        while (!source.eol()) {
            char c = source.peek();
            if (Character.isWhitespace(c)) {
                break;
            }
            source.pop();
            text.append(c);
        }

        //do we need to validate the verb against GET, POST, etc. ?
        String verb = text.toString().trim();
        if (verb.isEmpty()) {
            throw new ParseException("Missing verb", source.currentLine(), source.row(), source.col());
        }
        return verb;
    }

    RoutesParser.ParsedPath parsePath(String prefix) {
        eatWhitespace(source);
        if (source.eol()) {
            throw new ParseException("Missing path", source.currentLine(), source.row(), source.col());
        }
        Set<String> variablesNames = new HashSet<>();
        StringBuilder pathPattern = new StringBuilder();

        boolean relativePath = source.peek() != '/';

        boolean done = false;
        while (!source.eol() && !done) {
            char c = source.peek();
            if (Character.isWhitespace(c)) {
                done = true;
            } else if (found('{')) {
                readPathVariable(pathPattern, variablesNames);
            } else if (found('*')) {
                readPathGlob(pathPattern);
            } else {
                readPathText(pathPattern);
            }
        }

        if (relativePath) {
            pathPattern.insert(0, ".*?");
        }
        //FIXME: think about this: add prefix before or after the insertion of the relativePath wildcard ??
        if (pathPattern.length() > 2 && "\\Q".equals(pathPattern.substring(0, 2))) {
            pathPattern.insert(2, prefix);
        } else {
            pathPattern.insert(0, Pattern.quote(prefix));
        }
        return new RoutesParser.ParsedPath(Pattern.compile(pathPattern.toString()), variablesNames);
    }

    private void eatWhitespace(Source source) {
        while (!source.eol() && Character.isWhitespace(source.peek())) {
            source.pop();
        }
    }

    private boolean validVarName(String varName) {
        try {
            Pattern.compile("(?<" + varName + ">.*)");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void readPathVariable(StringBuilder pathPattern, Set<String> variablesNames) {
        eatWhitespace(source);
        int startPos = source.col();
        StringBuilder text = new StringBuilder();
        String pattern = null;
        boolean done = false;
        boolean multiSeg = false;
        while (!source.eol() && !done) {
            char c = source.peek();
            if (c == '*') {
                source.pop();
                multiSeg = true;
                done = true;
            } else if (Character.isWhitespace(c) || c == ':' || c == '}') {
                done = true;
            } else {
                source.pop();
                text.append(c);
            }
        }

        if (done) {
            String varName = text.toString();
            if (varName.isEmpty()) {
                throw new ParseException("Missing var name", source.currentLine(), source.row(), startPos);
            }
            if (!validVarName(varName)) {
                throw new ParseException("Invalid var name: '" + varName + "'", source.currentLine(), source.row(), startPos);
            }

            eatWhitespace(source);
            if (!source.eol() && source.peek() == ':') {
                if (multiSeg) {
                    throw new ParseException("Cannot combine a user specified regexp and a *-modifier for the path variable '" + varName + "'",
                            source.currentLine(), source.row(), startPos);

                }
                source.pop();//consume the :
                pattern = readPathVariablePattern(source);
            }
            eatWhitespace(source);
            if (source.eol()) {
                throw new ParseException("Unclosed path variable", source.currentLine(), source.row(), startPos - 1);
            } else if (source.peek() != '}') {
                throw new ParseException("Unexpected input (was expecting '}')", source.currentLine(), source.row(), source.col());
            }
            source.pop();//consume '}'
            if (pattern != null) {
                pathPattern.append("(?<").append(varName).append(">").append(pattern).append(")");
            } else {
                String s = multiSeg ? ".+?" : "[^/]+";
                pathPattern.append("(?<").append(varName).append(">").append(s).append(")");
            }

            if (!variablesNames.add(varName)) {
                throw new ParseException("Duplicate path variable name '" + varName + "'", source.currentLine(), source.row(), startPos);
            }
        } else {
            throw new ParseException("Unclosed path variable", source.currentLine(), source.row(), startPos - 1);
        }
    }

    private String readPathVariablePattern(Source source) {
        int balance = 0;
        StringBuilder text = new StringBuilder();
        boolean done = false;
        eatWhitespace(source);
        int startPos = source.col();
        while (!source.eol() && !done) {
            char c = source.peek();
            if (c == '}' && balance == 0) {
                done = true;
            } else {
                if (c == '{') {
                    balance++;
                } else if (c == '}') {
                    balance--;
                }
                source.pop();
                text.append(c);
            }
        }
        if (done) {
            String regex = text.toString().trim();
            if (regex.isEmpty()) {
                throw new ParseException("Empty regex", source.currentLine(), source.row(), startPos);

            }
            try {
                Pattern.compile(regex);
            } catch (Exception e) {
                throw new ParseException("Invalid regex: '" + regex + "'", source.currentLine(), source.row(), startPos, e);
            }
            return regex;
        } else {
            throw new ParseException("Incomplete route line: problematic variable pattern (extends to the end of the route line, most probably due to unbalanced {}).",
                    source.currentLine(), source.row(), startPos);
        }
    }

    private void readPathGlob(StringBuilder pathPattern) {
        if (!source.eol()) {
            if (found('*')) {
                pathPattern.append(".*?");//multi segment
                return;
            }

            // Do we need to ensure that only * and ** are accepted ?
            // To implement this, we'll need to be able to pushback popped chars
        }
        pathPattern.append("[^/]*?");//single segment

    }

    private void readPathText(StringBuilder pathPattern) {
        StringBuilder text = new StringBuilder();
        int startPos = 0;
        boolean done = false;
        while (!source.eol() && !done) {
            char c = source.peek();
            if (c == '*' || c == '{' || Character.isWhitespace(c)) {
                done = true;
            } else {
                source.pop();
                if (text.length() == 0) {
                    startPos = source.col();
                }
                text.append(c);
            }
        }
        //do we need to check for empty text ?
        pathPattern.append(Pattern.quote(text.toString().trim()));
    }


    private RouteResolution.Action readAction(String basePackage, Collection<String> variablesNames) {
        eatWhitespace(source);
        int actionStartPos = source.col();

        String actionName = readActionName(source);
        if ("pass".equals(actionName)) {
            return PassAction.INSTANCE;
        } else { // add if/else here to handle more predefined actions (redirect, forward, 404, etc.)
            // not a predefined action, read args

            // otherwise, must be of the for {controller}.{action}
            int i = actionName.lastIndexOf(".");
            if (i == -1 || i == actionName.length() - 1) {
                throw new ParseException("Invalid action name: should match the form {controller}.{action}", source.currentLine(), source.row(), actionStartPos);
            }
            String controllerId = actionName.substring(0, i);
            String methodName = actionName.substring(i + 1);
            List<ActionArgument> args = readActionArgs(basePackage, variablesNames);

            return new InvokeMethodAction(controllerId, methodName, args);
        }
    }

    private String readActionName(Source source) {
        eatWhitespace(source);
        if (source.eol()) {
            throw new ParseException("Missing action", source.currentLine(), source.row(), source.col());
        }

        StringBuilder text = new StringBuilder();
        boolean done = false;
        int startPos = source.col();
        while (!source.eol() && !done) {
            char c = source.peek();
            if (Character.isWhitespace(c) || c == '(') {
                done = true;
            } else {
                source.pop();
                text.append(c);
            }
        }

        String actionName = text.toString().trim();
        if (actionName.isEmpty()) {
            throw new ParseException("Missing action name", source.currentLine(), source.row(), source.col());
        }
        return actionName;
    }

    private List<ActionArgument> readActionArgs(String basePackage, Collection<String> variablesNames) {
        eatWhitespace(source);
        if (source.eol()) {
            throw new ParseException("Missing action arguments", source.currentLine(), source.row(), source.col());
        }

        if (source.pop() != '(') {
            throw new ParseException("Unexpected input (was expecting '(')", source.currentLine(), source.row(), source.col() - 1);
        }
        eatWhitespace(source);
        if (source.peek() == ')') {//we're done here
            source.pop();
            return Collections.emptyList();
        } else {
            List<ActionArgument> args = new ArrayList<>();

            args.add(readActionArgument(basePackage, variablesNames));
            boolean done = false;
            while (!source.eol() && !done) {
                char c = source.peek();
                if (c == ')') {
                    source.pop();//consume the ')'
                    done = true;
                } else if (c == ',') {
                    source.pop();//consume the ','
                    eatWhitespace(source);
                    args.add(readActionArgument(basePackage, variablesNames));
                    eatWhitespace(source);
                } else {//shouldn't happen, I think
                    throw new ParseException("Unexpected input while parsing the arguments (expecting one of ',' or ')')", source.currentLine(), source.row(), source.col());
                }
            }
            if (!done) {
                //we reached the EOL without seeing ')'
                throw new ParseException("Unclosed arguments list (missing ')')", source.currentLine(), source.row(), source.col());
            }
            return args;
        }
    }

    private ActionArgument readActionArgument(String basePackage, Collection<String> variablesNames) {
        char c = source.peek();
        if (c == '{') {
            return readReferenceArgument(variablesNames);
        } else if (c == '"') {
            return readStringArgument(source);
        } else {
            return readNumOrBoolOrNullOrCustomArgument(basePackage);
        }
    }


    private ActionArgument readReferenceArgument(Collection<String> variablesNames) {
        int startPos = source.col();
        source.pop();// consume the opening {
        eatWhitespace(source);
        int nameStartPos = source.col();
        StringBuilder text = new StringBuilder();
        Class<?>[] types = null;
        boolean done = false;
        while (!source.eol() && !done) {
            char c = source.pop();
            if (c == '}') {
                done = true;
            } else if (c == ':') {
                types = readReferenceType(startPos);
                eatWhitespace(source);
                if (!source.eol()) {
                    if (source.peek() == '}') {
                        source.pop();
                        done = true;
                    } else {
                        throw new ParseException("Unexpected input (was expecting '}')", source.currentLine(), source.row(), source.col());
                    }
                } else {
                    throw new ParseException("Unclosed reference argument", source.currentLine(), source.row(), startPos);
                }
            } else {
                text.append(c);
            }
        }

        if (!done) {
            throw new ParseException("Unclosed reference argument", source.currentLine(), source.row(), startPos);
        }

        String value = text.toString().trim();
        if (value.isEmpty()) {
            throw new ParseException("Missing reference name", source.currentLine(), source.row(), startPos);
        }

        String s;
        if ("req.body".equals(value)) {
            return new ActionArgument.RequestBody();
        } else if ((s = checkAndRemovePrefix(value, "req.param.")) != null) {
            return fillInTypes(new ActionArgument.RequestParameter(s), types);
        } else if ((s = checkAndRemovePrefix(value, "req.header.")) != null) {
            return fillInTypes(new ActionArgument.Header(s), types);
        } else if ((s = checkAndRemovePrefix(value, "req.cookie.")) != null) {
            return fillInTypes(new ActionArgument.CookieArgument(s), types);
        } else if ((s = checkAndRemovePrefix(value, "req.part.")) != null) {
            return new ActionArgument.RequestPart(s);
        } else if (variablesNames.contains(value)) {
            return fillInTypes(new ActionArgument.Variable(value), types);
        } else {
            throw new ParseException("Invalid reference name '" + value + "': Isn't one of req.param.<x>, req.header.<x>, req.cookie.<x>, req.part.<x> or one of the path variables " + variablesNames, source.currentLine(), source.row(), nameStartPos);
        }

    }

    private ActionArgument fillInTypes(ActionArgument.ChameleonArgument argument, Class<?>[] types) {
        if (types != null) {
            if (types.length == 1) {
                argument.type = types[0];
            } else if (types.length == 2) {
                argument.type = types[0];
                argument.typeArg = types[1];
            } else {
                throw new IllegalArgumentException("Unhandled case (3 types), most probably a bug");
            }
        }
        return argument;
    }

    private static final Map<String, Class<?>> TYPES_NAMES;

    static {
        TYPES_NAMES = new HashMap<>();
        TYPES_NAMES.put("byte", Byte.TYPE);
        TYPES_NAMES.put("short", Short.TYPE);
        TYPES_NAMES.put("int", Integer.TYPE);
        TYPES_NAMES.put("long", Long.TYPE);
        TYPES_NAMES.put("float", Float.TYPE);
        TYPES_NAMES.put("double", Double.TYPE);

        TYPES_NAMES.put("Byte", Byte.class);
        TYPES_NAMES.put("Short", Short.class);
        TYPES_NAMES.put("Integer", Integer.class);
        TYPES_NAMES.put("Long", Long.class);
        TYPES_NAMES.put("Float", Float.class);
        TYPES_NAMES.put("Double", Double.class);

        TYPES_NAMES.put("boolean", Boolean.TYPE);
        TYPES_NAMES.put("Boolean", Boolean.class);

        TYPES_NAMES.put("BigInteger", BigInteger.class);
        TYPES_NAMES.put("BigDecimal", BigDecimal.class);

        TYPES_NAMES.put("String", String.class);
    }

    private Class<?>[] readReferenceType(int referenceStartPos) {
        eatWhitespace(source);
        if (source.eol()) {
            throw new ParseException("Incomplete route line: Missing type definition", source.currentLine(), source.row(), source.col());
        }

        StringBuilder text = new StringBuilder();
        boolean done = false;
        int startPos = source.col();
        while (!source.eol() && !done) {
            char c = source.peek();
            if (Character.isWhitespace(c) || c == '}') {
                done = true;
            } else {
                source.pop();
                text.append(c);
            }
        }

        String typeName = text.toString().trim();

        if (typeName.isEmpty()) {
            throw new ParseException("Missing type definition", source.currentLine(), source.row(), source.col());
        }

        if (!done) {
            throw new ParseException("Unclosed reference argument argument", source.currentLine(), source.row(), referenceStartPos);
        }


        String primitiveTypes = "int|long|float|double|short|byte|boolean";
        String objectTypes = "Integer|Long|Float|Double|Short|Byte|Boolean|BigInteger|BigDecimal|String";
        String types = primitiveTypes + "|" + objectTypes;
        String collTypes = "\\[(" + objectTypes + ")\\]";

        if (typeName.matches(types)) {
            return new Class<?>[]{TYPES_NAMES.get(typeName)};
        } else if (typeName.matches(collTypes)) {
            return new Class<?>[]{Collection.class, TYPES_NAMES.get(typeName.substring(1, typeName.length() - 1))};
        } else {
            throw new ParseException("Unknown type '" + typeName + "'. Supported types are " + types + " or [oneOfTheSupportedTypes]", source.currentLine(), source.row(), startPos);
        }
    }

    /**
     * Ensures that a string starts with a prefix, returning what comes afterwards if true, null otherwise
     */

    private String checkAndRemovePrefix(String s, String prefix) {
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        } else {
            return null;
        }
    }


    private ActionArgument readStringArgument(Source source) {
        int startPos = source.col();
        source.pop();// consume the opening "
        StringBuilder text = new StringBuilder();
        boolean done = false;
        boolean inEscape = false;
        while (!source.eol() && !done) {
            char c = source.pop();
            if (c == '"') {
                if (inEscape) {
                    text.append(c);// we encountered \", so the String hasn't ended yet, insert the " in it
                    inEscape = false;
                } else {
                    done = true;
                }
            } else if (c == '\\' && !inEscape) {
                inEscape = true;
                // don't add the \ to the text yet, it may be escaping a "
            } else {
                if (inEscape) {
                    // we encountered a \, and then something other than ", so it means nothing to the parser, we just add it to the text
                    text.append('\\');
                    inEscape = false;
                }
                text.append(c);
            }
        }

        if (!done) {
            throw new ParseException("Unnclosed String argument", source.currentLine(), source.row(), startPos);
        }

        String value = text.toString();// don't trim
        System.err.println("String='" + value + "'");
        return new ActionArgument.Const<>(value);
    }

    private ActionArgument readNumOrBoolOrNullOrCustomArgument(String basePackage) {
        int startPos = source.col();
        StringBuilder text = new StringBuilder();
        boolean done = false;
        while (!source.eol() && !done) {
            char c = source.peek();
            if (Character.isWhitespace(c) || c == ',' || c == ')') {
                done = true;
            } else {
                source.pop();
                text.append(c);
            }
        }

        String value = text.toString().trim();
        if (value.isEmpty()) {
            throw new ParseException("Empty argument", source.currentLine(), source.row(), source.col());
        } else if ("null".equals(value)) {
            return new ActionArgument.Const<>(null);
        } else if ("true".equals(value) || "false".equals(value)) {
            return new ActionArgument.Const<>(Boolean.parseBoolean(value));
        } else {
            try {
                return new NumConst(new BigDecimal(value));
            } catch (NumberFormatException nfe) {
                try {
                    return lookupCustomArgument(value, basePackage);
                } catch (Exception e) {
                    //TODO: before giving up, look for a custom ActionArgument named value
                    throw new ParseException("Unknown argument '" + value + "'. Was expecting one of null, true, false or a numeric, which isn't the case.\n" +
                            "Also, when assuming it is a custom ActionArgument class, the following error ocured: " + e.getMessage(), source.currentLine(), source.row(), startPos);
                }
            }
        }
    }

    private static ActionArgument lookupCustomArgument(String value, String basePackage) {
        Class<?> clazz;
        try {
            clazz = ReflectUtils.forName(value);
        } catch (ClassNotFoundException e) {
            String value2 = basePackage + "." + value;
            try {
                clazz = ReflectUtils.forName(value2);
            } catch (ClassNotFoundException e1) {
                throw new ConfigException("Class not found' : Tried with " + value + " and " + value2 + " but none were found");
            }
        }

        try {
            if (!ActionArgument.class.isAssignableFrom(clazz)) {
                throw new ConfigException(clazz + " has to implement ActionArgument");
            }
            return clazz != null ? (ActionArgument) clazz.newInstance() : null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigException("Can't create an instance of the ActionArgument " + clazz);
        }
    }

}
