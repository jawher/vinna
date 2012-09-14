package vinna.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class IterBlock implements BlockHandler {

    @Override
    public void render(LiquidbarsNode node, Context context, BlockHandler defaultBlockHandler, Writer out) throws IOException {
        LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
        Object value = context.resolve(block.getArg());
        Collection<Object> coll = (Collection<Object>) value;
        int i = 0;
        for (Object o : coll) {
            Context subContext = new Context(context, o);
            subContext.add("#", i);
            subContext.add("##", i + 1);
            subContext.add("#first", i == 0);
            subContext.add("#last", i == coll.size() - 1);

            for (LiquidbarsNode child : block.getChildren()) {
                defaultBlockHandler.render(child, subContext, defaultBlockHandler, out);
            }
            i++;
        }
    }
}
