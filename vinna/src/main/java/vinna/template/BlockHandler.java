package vinna.template;

import java.io.IOException;
import java.io.Writer;

public interface BlockHandler {
    void render(LiquidbarsNode node, Context context, BlockHandler defaultBlockHandler, Writer out) throws IOException;
}
