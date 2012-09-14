package vinna.template;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Liquidbars {
    public interface TemplateLoader {
        Reader load(String name);
    }

    private Map<String, BlockHandler> handlers = new HashMap<String, BlockHandler>();
    private TemplateLoader templateLoader = new TemplateLoader() {
        @Override
        public Reader load(String name) {
            try {
                return new InputStreamReader(Liquidbars.class.getResourceAsStream(name), "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public Liquidbars() {
        registerHandler("if", new IfBlock());
        registerHandler("for", new IterBlock());
    }

    public final Liquidbars registerHandler(String name, BlockHandler handler) {
        handlers.put(name, handler);
        return this;
    }

    public Liquidbars templateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
        return this;
    }

    public Template parse(String name) {
        return parse(templateLoader.load(name));
    }

    public Template parse(Reader reader) {
        List<LiquidbarsNode> rootNodes = new LiquidbarsParser(reader).parse();
        return new Template(rootNodes, this);
    }

    /* package */ BlockHandler defaultRenderer = new BlockHandler() {

        @Override
        public void render(LiquidbarsNode node, Context context, BlockHandler defaultBlockHandler, Writer out) throws IOException {
            if (node instanceof LiquidbarsNode.Text) {
                out.write(((LiquidbarsNode.Text) node).getValue());
            } else if (node instanceof LiquidbarsNode.Variable) {
                Object value = context.resolve(((LiquidbarsNode.Variable) node).getName());
                out.write(String.valueOf(value));
            } else {
                LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
                BlockHandler handler = handlers.get(block.getName());
                if (handler == null) {
                    throw new RuntimeException("No handler for block " + block.getName());
                } else {
                    handler.render(node, context, defaultBlockHandler, out);
                }
            }
        }
    };

    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }
}
