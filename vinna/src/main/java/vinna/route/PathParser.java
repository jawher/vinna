package vinna.route;


import vinna.exception.ConfigException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PathParser {
    public class PathParseException extends ConfigException {
        private final String message;
        private final String path;
        private int col;

        public PathParseException(String message, String path, int col) {
            super(message);
            this.message = message;
            this.path = path;
            this.col = col;
        }

        public PathParseException(String message, String path, Token where) {
            super(message);
            this.message = message;
            this.path = path;
            this.col = where.col;
        }

        @Override
        public String getMessage() {
            StringBuilder res = new StringBuilder(message);

            if (path != null) {
                res.append("\n").append(path).append("\n");
                for (int i = 0; i < col; i++) {
                    res.append(" ");
                }
                res.append("^");
            }
            return res.toString();
        }


    }

    private static class Token {
        public enum Type {
            TEXT, VAR, PATTERN
        }

        public final Type type;
        public final String value;
        public final int col;

        private Token(Type type, String value, int col) {
            this.type = type;
            this.value = value;
            this.col = col;
        }

        @Override
        public String toString() {
            return type + "('" + value + "')";
        }
    }

    private enum State {
        IN_TEXT, IN_NAME, IN_PATTERN
    }

    public List<Token> tokenize(String path) {
        List<Token> res = new ArrayList<>();
        State state = State.IN_TEXT;
        StringBuilder text = new StringBuilder();
        int startPos = 0;
        int balance = 0;
        int escape = 0;
        for (int col = 0; col < path.length(); col++) {
            char c = path.charAt(col);
            if (escape > 0) {
                escape--;
            }
            switch (state) {
                case IN_TEXT:
                    if (c == '{') {
                        if (text.length() > 0) {
                            res.add(new Token(Token.Type.TEXT, text.toString(), startPos));
                            text = new StringBuilder();
                        }
                        state = State.IN_NAME;
                    } else {
                        if (text.length() == 0) {
                            startPos = col;
                        }
                        text.append(c);
                    }
                    break;
                case IN_NAME:
                    if (c == '}') {
                        if (text.length() > 0) {
                            res.add(new Token(Token.Type.VAR, text.toString().trim(), startPos));
                            text = new StringBuilder();
                        }
                        state = State.IN_TEXT;
                    } else if (c == ':') {
                        if (text.length() > 0) {
                            res.add(new Token(Token.Type.VAR, text.toString().trim(), startPos));
                            text = new StringBuilder();
                        } else {
                            res.add(new Token(Token.Type.VAR, "", col));
                            text = new StringBuilder();
                        }
                        state = State.IN_PATTERN;
                    } else {
                        if (text.length() == 0) {
                            startPos = col;
                        }
                        text.append(c);
                    }
                    break;
                case IN_PATTERN:
                    if (c == '}' && escape == 0) {
                        if (balance > 0) {
                            balance--;
                            text.append(c);
                        } else {
                            if (text.length() > 0) {
                                res.add(new Token(Token.Type.PATTERN, text.toString().trim(), startPos));
                                text = new StringBuilder();
                            } else {
                                res.add(new Token(Token.Type.PATTERN, "", col - 1));
                            }
                            state = State.IN_TEXT;
                        }
                    } else {
                        if (text.length() == 0) {
                            startPos = col;
                        }
                        text.append(c);
                        if (c == '\\') {
                            escape = 2;
                        } else if (c == '{' && escape == 0) {
                            balance++;
                        }
                    }
                    break;
            }

        }

        if (state == State.IN_TEXT) {
            if (text.length() > 0) {
                res.add(new Token(Token.Type.TEXT, text.toString().trim(), startPos));
            }
        } else {
            throw new RuntimeException("Unbalanced expression (check your {})");
        }


        return res;
    }

    private String handleGlobs(String text) {
        StringBuilder res = new StringBuilder();
        StringBuilder segment = new StringBuilder();
        int startCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '*') {
                if (segment.length() > 0) {
                    res.append(Pattern.quote(segment.toString()));

                    segment = new StringBuilder();
                }
                startCount++;
            } else {
                if (startCount > 2) {
                    throw new RuntimeException("Accepted globs are ** and *");
                } else if (startCount == 1) {
                    res.append("[^/]+?");
                } else if (startCount == 2) {
                    res.append(".+?");
                }
                startCount = 0;
                segment.append(c);
            }
        }
        if (segment.length() > 0) {
            res.append(Pattern.quote(segment.toString()));
        } else if (startCount > 2) {
            throw new RuntimeException("Accepted globs are ** and *");
        } else if (startCount == 1) {
            res.append("[^/]+?");
        } else if (startCount == 2) {
            res.append(".+?");
        }
        return res.toString();
    }

    public Pattern compile(String path) {
        List<Token> tokens = tokenize(path);
        StringBuilder pattern = new StringBuilder();
        String varName = null;
        boolean multiSeg = false;
        for (Token token : tokens) {
            switch (token.type) {
                case TEXT:
                    if (varName != null) {
                        pattern.append("(?<").append(varName).append(">").append(multiSeg ? ".+?" : "[^/]+").append(")");
                        varName = null;
                    }
                    pattern.append(handleGlobs(token.value));
                    break;
                case VAR:
                    String name = token.value.trim();
                    if (name.isEmpty()) {
                        throw new PathParseException("A path variable name is required", path, token.col);
                    } else {
                        if (name.endsWith("*")) {
                            varName = name.substring(0, name.length() - 1);
                            multiSeg = true;
                        } else {
                            varName = name;
                            multiSeg = false;
                        }

                        //validate the user supplied group name
                        try {
                            Pattern.compile("(?<" + varName + ">.*)");
                        } catch (PatternSyntaxException e) {
                            throw new PathParseException("Invalid path variable name \"" + varName + "\": only letters and digits are allowed (with an optional trailing *)", path, token.col);
                        }
                    }
                    break;
                case PATTERN:
                    String segPat = token.value.trim();
                    if (segPat.isEmpty()) {
                        throw new PathParseException("A path variable pattern is required", path, token.col);
                    }
                    if (multiSeg) {
                        throw new PathParseException("Cannot combine a user specified regexp and a *-modifier", path, token);
                    } else {
                        //validate the user supplied pattern
                        try {
                            Pattern.compile(segPat);
                        } catch (PatternSyntaxException e) {
                            throw new PathParseException("Invalid pattern \"" + segPat + "\": " + e.getMessage(), path, token.col);
                        }
                        pattern.append("(?<").append(varName).append(">").append(segPat).append(")");
                        varName = null;
                    }
                    break;
            }
        }
        if(!path.startsWith("/")) {
            pattern.insert(0, ".*?");
        }
        return Pattern.compile(pattern.toString());
    }

    public static void main(String[] args) {
        PathParser pathParser = new PathParser();
        Pattern pattern = pathParser.compile("/*{ a : \\d+}/*/{b}/**/*.js");
        System.err.println(pattern);
        System.err.println(pattern.matcher("a/x5/bvc/bbb/a/b/c/angular.js").matches());
    }
}
