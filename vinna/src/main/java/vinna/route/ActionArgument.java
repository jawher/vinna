package vinna.route;

import vinna.request.Request;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public interface ActionArgument {

    public static class Environment {
        protected final Map<String, String> matchedVars;
        protected final Request request;

        public Environment(Request request, Map<String, String> matchedVars) {
            this.matchedVars = matchedVars;
            this.request = request;
        }
    }

    public static class Const<T> implements ActionArgument {
        private final T value;

        public Const(T value) {
            this.value = value;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            return value;
        }
    }

    public static class Variable extends CastArgument {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            String value = env.matchedVars.get(name);
            return castValue(targetType, value);
        }
    }

    public static class Headers implements ActionArgument {

        private final String headerName;

        public Headers(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            return env.request.getHeaders(headerName);
        }
    }

    public static class Header extends CastArgument {

        private final String headerName;

        public Header(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            return castValue(targetType, env.request.getHeader(headerName));
        }
    }

    public abstract class CastArgument implements ActionArgument {

        protected Object castValue(Class<?> targetType, String value) {
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

    Object resolve(Environment env, Class<?> targetType);

}
