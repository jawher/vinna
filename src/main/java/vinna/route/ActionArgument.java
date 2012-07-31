package vinna.route;

import vinna.request.Request;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public abstract class ActionArgument {

    public static class Environment {
        protected final Map<String, String> matchedVars;
        protected final Request request;
        protected final Class<?> targetType;

        public Environment(Request request, Map<String, String> matchedVars, Class<?> targetType) {
            this.matchedVars = matchedVars;
            this.request = request;
            this.targetType = targetType;
        }
    }

    public static class Const<T> extends ActionArgument {
        private final T value;

        public Const(T value) {
            this.value = value;
        }

        @Override
        protected Object resolve(Environment env) {
            return value;
        }
    }

    public static class Variable extends ActionArgument {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        protected Object resolve(Environment env) {
            String value = env.matchedVars.get(name);
            if (Long.class.equals(env.targetType) || Long.TYPE.equals(env.targetType)) {
                return Long.parseLong(value);
            } else if (Integer.class.equals(env.targetType) || Integer.TYPE.equals(env.targetType)) {
                return Integer.parseInt(value);
            } else if (Short.class.equals(env.targetType) || Short.TYPE.equals(env.targetType)) {
                return Short.parseShort(value);
            } else if (Byte.class.equals(env.targetType) || Byte.TYPE.equals(env.targetType)) {
                return Byte.parseByte(value);
            } else if (Double.class.equals(env.targetType) || Double.TYPE.equals(env.targetType)) {
                return Double.parseDouble(value);
            } else if (Float.class.equals(env.targetType) || Float.TYPE.equals(env.targetType)) {
                return Float.parseFloat(value);
            } else if (BigDecimal.class.equals(env.targetType)) {
                return new BigDecimal(value);
            } else if (BigInteger.class.equals(env.targetType)) {
                return new BigInteger(value);
            } else if (Boolean.class.equals(env.targetType) || Boolean.TYPE.equals(env.targetType)) {
                return Boolean.parseBoolean(value);
            } else if (String.class.equals(env.targetType)) {
                return value;
            }
            //TODO: handle other types
            throw new IllegalArgumentException("Unsupported conversion of '" + value + "' to " + env.targetType);
        }

        public final long asLong() {
            return 42;
        }

        public final int asInt() {
            return 42;
        }

        public final short asShort() {
            return 42;
        }

        public final byte asByte() {
            return 42;
        }

        public final float asFloat() {
            return 42.0f;
        }

        public final double asDouble() {
            return 42.0;
        }

        public final BigDecimal asBigDecimal() {
            return BigDecimal.TEN;
        }

        public final BigInteger asBigInteger() {
            return BigInteger.TEN;
        }

        public final String asString() {
            return "42";
        }

        public final boolean asBoolean() {
            return false;
        }
    }

    public static class Headers extends ActionArgument {

        private final String headerName;

        public Headers(String headerName) {
            this.headerName = headerName;
        }

        @Override
        protected Object resolve(Environment env) {
            return env.request.getHeaders(headerName);
        }
    }

    public static class Header extends ActionArgument {

        private final String headerName;

        public Header(String headerName) {
            this.headerName = headerName;
        }

        @Override
        protected Object resolve(Environment env) {
            return env.request.getHeader(headerName);
        }
    }

    protected abstract Object resolve(Environment env);

}
