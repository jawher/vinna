package vinna.route;

import java.math.BigDecimal;
import java.math.BigInteger;
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
            if (Long.class.equals(targetType) || Long.TYPE.equals(targetType)) {
                return Long.parseLong(value);
            } else if (Integer.class.equals(targetType) || Integer.TYPE.equals(targetType)) {
                return Integer.parseInt(value);
            } else if (Short.class.equals(targetType) || Short.TYPE.equals(targetType)) {
                return Short.parseShort(value);
            } else if (Byte.class.equals(targetType) || Byte.TYPE.equals(targetType)) {
                return Byte.parseByte(value);
            } else if (Double.class.equals(targetType) || Double.TYPE.equals(targetType)) {
                return Double.parseDouble(value);
            } else if (Float.class.equals(targetType) || Float.TYPE.equals(targetType)) {
                return Float.parseFloat(value);
            } else if (BigDecimal.class.equals(targetType)) {
                return new BigDecimal(value);
            } else if (BigInteger.class.equals(targetType)) {
                return new BigInteger(value);
            } else if (Boolean.class.equals(targetType) || Boolean.TYPE.equals(targetType)) {
                return Boolean.parseBoolean(value);
            } else if (String.class.equals(targetType)) {
                return value;
            }
            //TODO: handle other types
            throw new IllegalArgumentException("Unsupported conversion of '" + value + "' to " + targetType);
        }

        public final long toLong() {
            return 42;
        }

        public final int toInt() {
            return 42;
        }

        public final short toShort() {
            return 42;
        }

        public final byte toByte() {
            return 42;
        }

        public final float toFloat() {
            return 42.0f;
        }

        public final double toDouble() {
            return 42.0;
        }

        public final BigDecimal toBigDecimal() {
            return BigDecimal.TEN;
        }

        public final BigInteger toBigInteger() {
            return BigInteger.TEN;
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
