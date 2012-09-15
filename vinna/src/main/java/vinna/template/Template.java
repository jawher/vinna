package vinna.template;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template {
    private List<LiquidbarsNode> rootNodes;
    private Liquidbars liquidbars;

    public Template(List<LiquidbarsNode> rootNodes, Liquidbars liquidbars) {
        this.rootNodes = rootNodes;
        this.liquidbars = liquidbars;
        processIncludes();
        processExtends();
    }

    private void processIncludes() {
        List<LiquidbarsNode> mergedNodes = new ArrayList<>(rootNodes.size());
        for (LiquidbarsNode node : rootNodes) {
            if (node instanceof LiquidbarsNode.Block) {
                LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
                if ("include".equals(block.getName())) {
                    final Template included = liquidbars.parse(block.getArg());
                    mergedNodes.addAll(included.getRootNodes());
                } else {
                    mergedNodes.add(block);
                }
            } else {
                mergedNodes.add(node);
            }
        }

        this.rootNodes = mergedNodes;
    }

    private void processExtends() {
        LiquidbarsNode.Block extend = null;
        boolean nonText = false;
        for (LiquidbarsNode node : rootNodes) {
            if (node instanceof LiquidbarsNode.Variable) {
                nonText = true;
                if (extend != null) {
                    throw new RuntimeException("Invalid template: it extends another template yet it defines a top level variable " + node);
                }
            } else if (node instanceof LiquidbarsNode.Block) {
                LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
                if ("extends".equals(block.getName())) {
                    if (extend != null) {
                        throw new RuntimeException("Invalid template: multiple extends directives: found " + node + " while " + extend + " was already defined");
                    } else if (nonText) {
                        throw new RuntimeException("Invalid template: it extends another template yet it defines a top level variable or block");
                    } else {
                        extend = block;
                    }
                } else {
                    nonText = true;
                    if (extend != null) {
                        throw new RuntimeException("Invalid template: it extends another template yet it defines a top level block " + node);
                    }
                }
            }
        }

        if (extend != null) {
            Template parent = liquidbars.parse(extend.getArg());
            final Map<String, List<LiquidbarsNode>> defines = processDefines(extend);
            List<LiquidbarsNode> mergedNodes = new ArrayList<>(parent.rootNodes.size());
            for (LiquidbarsNode node : parent.rootNodes) {
                if (node instanceof LiquidbarsNode.Block) {
                    final LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
                    if ("placeholder".equals(block.getName())) {
                        List<LiquidbarsNode> toInsert = defines.get(block.getArg());
                        if (toInsert != null) {
                            mergedNodes.addAll(toInsert);
                        } else {
                            mergedNodes.addAll(block.getChildren());
                        }
                    } else {
                        mergedNodes.add(block);
                    }
                } else {
                    mergedNodes.add(node);
                }
            }
            this.rootNodes = mergedNodes;
        }
    }

    private Map<String, List<LiquidbarsNode>> processDefines(LiquidbarsNode.Block extend) {
        Map<String, List<LiquidbarsNode>> defines = new HashMap<>();
        for (LiquidbarsNode node : extend.getChildren()) {
            if (node instanceof LiquidbarsNode.Variable) {
                throw new RuntimeException("Invalid template: the variable " + node + " must appear inside a define block");
            } else if (node instanceof LiquidbarsNode.Block) {
                LiquidbarsNode.Block block = (LiquidbarsNode.Block) node;
                if ("define".equals(block.getName())) {
                    defines.put(block.getArg(), block.getChildren());
                } else {
                    throw new RuntimeException("Invalid template: the block " + node + " must appear inside a define block");
                }
            }
        }
        return defines;
    }

    public List<LiquidbarsNode> getRootNodes() {
        return rootNodes;
    }

    public void render(Object model, Writer out) {
        render(model, null, out);
    }

    public void render(Object model, Object helper, Writer out) {
        Context context = new Context(null, model, helper);
        try {
            for (LiquidbarsNode node : rootNodes) {
                liquidbars.defaultRenderer.render(node, context, liquidbars.defaultRenderer, out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Template " + rootNodes;
    }
}
