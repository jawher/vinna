package vinna.template;

import java.util.List;

public abstract class LiquidbarsNode {

    public static class Text extends LiquidbarsNode {
        private String value;

        public Text(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class Variable extends LiquidbarsNode {
        private String name;
        private boolean raw;

        public Variable(String name, boolean raw) {
            this.name = name;
            this.raw = raw;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRaw() {
            return raw;
        }

        public void setRaw(boolean raw) {
            this.raw = raw;
        }

        @Override
        public String toString() {
            return "{{" + name + "}}";
        }
    }

    public static class Block extends LiquidbarsNode {
        private String name;
        private String arg;
        private List<LiquidbarsNode> children;

        public Block(String name, String arg, List<LiquidbarsNode> children) {
            this.name = name;
            this.arg = arg;
            this.children = children;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArg() {
            return arg;
        }

        public void setArg(String arg) {
            this.arg = arg;
        }

        public List<LiquidbarsNode> getChildren() {
            return children;
        }

        public void setChildren(List<LiquidbarsNode> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return "{{#" + name + (arg == null ? "" : " " + arg) + "}}\n\t" + children + "\n{{/" + name + "}}";
        }
    }
}
