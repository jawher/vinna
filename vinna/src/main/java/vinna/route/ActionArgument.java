package vinna.route;

import vinna.request.Request;
import vinna.util.Conversions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    public static class Variable extends ChameleonArgument {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            String value = env.matchedVars.get(name);
            return Conversions.convertString(value, targetType);
        }
    }

    public static class Headers implements ActionArgument {

        public Headers() {
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            return env.request.getHeaders();
        }
    }

    public static class Header extends ChameleonArgument {

        private final String headerName;
        private Class<?> collectionType;

        public Header(String headerName) {
            this.headerName = headerName;
        }

        public Header(String headerName, Class<?> collectionType) {
            this(headerName);
            this.collectionType = collectionType;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {

            if (collectionType != null) {
               return Conversions.convertCollection(env.request.getHeaders(headerName), collectionType);
            }
            return Conversions.convertString(env.request.getHeader(headerName), targetType);
        }

        public final <T> Collection<T> asCollection(Class<T> clazz) {
            this.collectionType = clazz;
            // TODO homemade collection implementation with unsupported operation ?
            return Collections.emptyList();
        }
    }

    public static abstract class ChameleonArgument implements ActionArgument {

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
