package vinna.route;

import java.util.Map;

public abstract class ActionArgument {

    public static class Const<T> extends ActionArgument {
        private final T value;

        public Const(T value) {
            this.value = value;
        }

        @Override
        protected Object resolve(Map<String, String> matchedVars, Class<?> targetType) {
            return value;
        }
    }

    public static class Variable extends ActionArgument {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        protected Object resolve(Map<String, String> matchedVars, Class<?> targetType) {
            String value = matchedVars.get(name);
            if (Integer.class.equals(targetType) || Integer.TYPE.equals(targetType)) {
                return Integer.parseInt(value);
            } else if (Boolean.class.equals(targetType) || Boolean.TYPE.equals(targetType)) {
                return Boolean.parseBoolean(value);
            } else if (String.class.equals(targetType)) {
                return value;
            }
            throw new IllegalArgumentException("Unsupported conversion of '" + value + "' to " + targetType);
        }

        public final int toInt() {
            return 42;
        }

        public final String toString() {
            return "42";
        }

        public final boolean toBoolean() {
            return false;
        }
    }

    //FIXME: ugly ugly matchedVars map argument. Maybe replace with an env type, encapsulating matched vars, sys
    // properties and special variables (req.body, req.headers.*, etc.)
    protected abstract Object resolve(Map<String, String> matchedVars, Class<?> targetType);


}
