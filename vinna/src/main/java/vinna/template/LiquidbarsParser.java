package vinna.template;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regexp FTW !
 */
public class LiquidbarsParser {

    private final Map<String, BlockHandler> handlers;

    private static class Token {
        private enum Type {
            TEXT, OPEN_VAR, OPEN_TAG, CLOSE_VAR, CLOSE_TAG, OPEN_RAW_VAR, CLOSE_RAW_VAR, EOF
        }

        public final Type type;
        public final String value;
        public final int row;
        public final int col;

        private Token(Type type, String value, int row, int col) {
            this.type = type;
            this.value = value;
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return type + ": '" + value + "' @ " + row + ":" + col;
        }
    }

    private static final String OPEN_RAW_VAR_S = "{{{";
    private static final String OPEN_VAR_S = "{{";
    private static final String CLOSE_RAW_VAR_S = "}}}";
    private static final String CLOSE_VAR_S = "}}";
    private static final String OPEN_TAG_S = "{%";
    private static final String CLOSE_TAG_S = "%}";

    private static final Pattern DELIMITERS = Pattern.compile(
            Pattern.quote(OPEN_RAW_VAR_S) + "|" +
                    Pattern.quote(CLOSE_RAW_VAR_S) + "|" +
                    Pattern.quote(OPEN_VAR_S) + "|" +
                    Pattern.quote(CLOSE_VAR_S) + "|" +
                    Pattern.quote(OPEN_TAG_S) + "|" +
                    Pattern.quote(CLOSE_TAG_S));
    private BufferedReader reader;
    private String line;
    private Matcher lineMatcher;
    private Token eof = null;
    private int row = 1, col = 0;

    public LiquidbarsParser(Reader reader, Map<String, BlockHandler> handlers) {
        this.handlers = handlers;
        this.reader = new BufferedReader(reader);
    }

    private void nextLine() {
        try {
            line = reader.readLine();
            row++;
            col = 0;
            if (line == null) {
                eof = new Token(Token.Type.EOF, "$", row, col);
            } else {
                lineMatcher = DELIMITERS.matcher(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Token nextToken() {
        StringBuilder text = new StringBuilder();
        while (true) {
            if (eof != null) {
                return eof;
            }

            if (line == null || col >= line.length()) {
                nextLine();
                if (eof != null) {
                    if (text.length() > 0) {
                        return new Token(Token.Type.TEXT, text.toString(), col, row);//FIXME: col, row
                    } else {
                        return eof;
                    }
                }
            }

            if (lineMatcher.find(col)) {
                String caught = lineMatcher.group();
                if (lineMatcher.start() > col || text.length() > 0) {
                    text.append(line.substring(col, lineMatcher.start()));
                    final Token res = new Token(Token.Type.TEXT, text.toString(), col, row);
                    col = lineMatcher.start();
                    return res;
                } else {
                    Token.Type type;
                    switch (caught) {
                        case OPEN_RAW_VAR_S:
                            type = Token.Type.OPEN_RAW_VAR;
                            break;
                        case CLOSE_RAW_VAR_S:
                            type = Token.Type.CLOSE_RAW_VAR;
                            break;
                        case OPEN_VAR_S:
                            type = Token.Type.OPEN_VAR;
                            break;
                        case CLOSE_VAR_S:
                            type = Token.Type.CLOSE_VAR;
                            break;
                        case OPEN_TAG_S:
                            type = Token.Type.OPEN_TAG;
                            break;
                        case CLOSE_TAG_S:
                            type = Token.Type.CLOSE_TAG;
                            break;
                        default:
                            throw new RuntimeException("bug !");
                    }
                    Token res = new Token(type, caught, row, col);
                    col = lineMatcher.end();
                    return res;
                }
            } else {
                text.append(line.substring(col)).append("\n");
                line = null;
            }
        }
    }

    private Token current;
    private List<String[]> sectionsStack = new ArrayList<>();

    private void advance() {
        current = nextToken();
    }

    private boolean is(Token.Type type) {
        return current.type == type;
    }

    public List<LiquidbarsNode> parse() {
        final List<LiquidbarsNode> rootNodes = start();
        if (!is(Token.Type.EOF)) {
            throw new RuntimeException("Was expecting EOF but got " + current);
        }
        return rootNodes;
    }

    private List<LiquidbarsNode> start() {
        List<LiquidbarsNode> nodes = new ArrayList<LiquidbarsNode>();
        while (true) {
            advance();
            if (is(Token.Type.TEXT)) {
                nodes.add(new LiquidbarsNode.Text(current.value));
            } else if (is(Token.Type.OPEN_RAW_VAR)) {
                advance();
                if (is(Token.Type.TEXT)) {
                    nodes.add(new LiquidbarsNode.Variable(current.value.trim(), true));
                } else {
                    throw new RuntimeException("Was expecting a variable id but got " + current);
                }
                advance();
                if (!is(Token.Type.CLOSE_RAW_VAR)) {
                    throw new RuntimeException("Was expecting a close pair but got " + current);
                }
            } else if (is(Token.Type.OPEN_VAR)) {
                advance();
                if (is(Token.Type.TEXT)) {
                    nodes.add(new LiquidbarsNode.Variable(current.value.trim(), false));
                } else {
                    throw new RuntimeException("Was expecting a variable id but got " + current);
                }
                advance();
                if (!is(Token.Type.CLOSE_VAR)) {
                    throw new RuntimeException("Was expecting a close pair but got " + current);
                }
            } else if (is(Token.Type.OPEN_TAG)) {
                String name;
                String arg = null;
                advance();
                if (is(Token.Type.TEXT)) {
                    String[] names = current.value.trim().split("\\s+");
                    if (names.length != 1 && names.length != 2) {
                        throw new RuntimeException("tags can have a name and an optional arg in ''" + current);
                    }
                    name = names[0];
                    if (names.length > 1) {
                        arg = names[1];
                    }
                } else {
                    throw new RuntimeException("Was expecting a section name but got " + current);
                }

                advance();
                if (!is(Token.Type.CLOSE_TAG)) {
                    throw new RuntimeException("Was expecting %} but got " + current);
                }

                if ("end".equals(name)) {
                    if (sectionsStack.isEmpty()) {
                        throw new RuntimeException("Unexpected end tag found: no matching open tag " + current);
                    } else {
                        String[] openSectionData = popSectionName();
                        String openSectionName = openSectionData[0];
                        String openSectionArg = openSectionData[1];
                        if (arg != null && !arg.equals(openSectionName)) {
                            throw new RuntimeException("Unbalanced close tag found " + arg + " (was expecting close tag for " + openSectionName + ")");
                        }
                        return Arrays.<LiquidbarsNode>asList(new LiquidbarsNode.Block(openSectionName, openSectionArg, nodes));
                    }
                } else {
                    BlockHandler handler = handlers.get(name);
                    if (handler != null && !handler.wantsCloseTag()) {
                        nodes.add(new LiquidbarsNode.Block(name, arg, Collections.<LiquidbarsNode>emptyList()));
                    } else {
                        pushSectionName(name, arg);
                        nodes.addAll(start());
                    }
                }
            } else {
                return nodes;
            }
        }
    }

    private void pushSectionName(String name, String arg) {
        sectionsStack.add(new String[]{name, arg});
    }

    private String[] popSectionName() {
        String[] res = sectionsStack.get(sectionsStack.size() - 1);
        sectionsStack.remove(sectionsStack.size() - 1);
        return res;
    }

    public static void main(String[] args) {

        //testLex();
        bench();

        /*String text = "{% each items %}{% if visible %}-{ {{ text }}{{ else }}- ***\n{% end if %}{% end eachp %}";

        final HandlebarsParser3 parser = new HandlebarsParser3(new StringReader(text));

        try {
            System.out.println(parser.parse());
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
            System.err.println(text);
            System.err.println(nspaces(parser.current.col - 2) + "^");
            e.printStackTrace();
        }*/
    }

    private static void bench() {
        long t0 = 0;
        final int warmup = 5000;
        final int count = 10000;
        for (int i = 0; i < warmup + count; i++) {
            if (i == warmup) {
                t0 = System.currentTimeMillis();
            }
            final InputStreamReader templateReader = new InputStreamReader(LiquidbarsParser.class.getResourceAsStream("new-liquid.html"));
            Map<String, BlockHandler> xhandlers=new HashMap<>();
            xhandlers.put("else", new ElseBlock());
            final LiquidbarsParser parser = new LiquidbarsParser(templateReader, xhandlers);
            parser.parse();
        }
        long t1 = System.currentTimeMillis();
        System.err.println("parsing " + count + " times the template took " + ((t1 - t0)) + "ms");
    }

    private static String nspaces(int n) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < n; i++) {
            res.append(" ");
        }
        return res.toString();
    }

    private static void testLex() {
        final String text = "{% each items %}{% if visible %}-{ {{ text }}{% else %}- ***\n{% endif %}{% endeach %}";
        Map<String, BlockHandler> xhandlers=new HashMap<>();
        xhandlers.put("else", new ElseBlock());

        LiquidbarsParser parser = new LiquidbarsParser(new InputStreamReader(LiquidbarsParser.class.getResourceAsStream("new-liquid.html")), xhandlers);
        Token tk;
        do {
            tk = parser.nextToken();
            System.err.println(tk.type + ": " + tk.value);
        } while (tk.type != Token.Type.EOF);
    }
}
