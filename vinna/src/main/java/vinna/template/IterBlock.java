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
            Context subContext = new IterContext(context, o, i, i == coll.size() - 1);
            for (LiquidbarsNode child : block.getChildren()) {
                defaultBlockHandler.render(child, subContext, defaultBlockHandler, out);
            }
            i++;
        }
    }

    private static class IterContext extends Context {
        private final int index;
        private final boolean last;

        public IterContext(Context parent, Object root, int index, boolean last) {
            super(parent, root);
            this.index = index;
            this.last = last;
        }

        @Override
        public Object resolve(String key) {
            switch (key) {
                case "#":
                    return index;
                case "##":
                    return index + 1;
                case "#first":
                    return index == 0;
                case "#last":
                    return last;
                default:
                    return super.resolve(key);
            }
        }
    }
}
