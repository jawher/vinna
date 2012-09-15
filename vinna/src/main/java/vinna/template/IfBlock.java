package vinna.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class IfBlock implements BlockHandler {

    @Override
    public boolean wantsCloseTag() {
        return true;
    }

    @Override
    public void render(LiquidbarsNode node, Context context, BlockHandler defaultBlockHandler, Writer out) throws IOException {
        LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
        Object value = context.resolve(block.getArg());
        boolean doit = true;
        if (value == null) {
            doit = false;
        }
        if (value instanceof Boolean) {
            doit = (Boolean) value;
        }
        if (value instanceof Collection) {
            doit = !((Collection) value).isEmpty();
        }

        Context subContext = new Context(context, value);
        for (LiquidbarsNode child : block.getChildren()) {
            if (child instanceof LiquidbarsNode.Block && ("else".equals(((LiquidbarsNode.Block) child).getName()))) {
                doit = !doit;
            } else if (doit) {
                defaultBlockHandler.render(child, subContext, defaultBlockHandler, out);
            }
        }
    }
}
