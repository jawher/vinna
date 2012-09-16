package vinna.template;

import java.io.IOException;
import java.io.Writer;

public class ElseBlock implements BlockHandler {
    @Override
    public boolean wantsCloseTag() {
        return false;
    }

    @Override
    public void render(LiquidbarsNode node, Context context, BlockHandler defaultBlockHandler, Writer out) throws IOException {
        // nop. Daddy if will do just fine on his own.
    }
}
