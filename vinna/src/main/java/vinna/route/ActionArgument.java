package vinna.route;

import vinna.request.Request;
import vinna.util.Conversions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
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
            if (targetType.isAssignableFrom(Collection.class)) {
                //TODO: simply do not expose asCollection for path variables ?
                if (typeArg != null) {
                    Object convertedValue = Conversions.convertString(value, typeArg);
                    return Collections.unmodifiableCollection(Arrays.asList(convertedValue));
                } else {
                    throw new RuntimeException("need an argType when the target is a collection");
                }
            }

            return Conversions.convertString(value, targetType);
        }
    }

    public static class RequestParameter extends ChameleonArgument {

        private final String name;

        public RequestParameter(String name) {
            this.name = name;
        }

        public RequestParameter(String name, Class<?> collectionType) {
            type = Collection.class;
            typeArg = collectionType;
            this.name = name;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            if (targetType.isAssignableFrom(Collection.class)) {
                if (typeArg != null) {
                    return Conversions.convertCollection(env.request.getParams(name), typeArg);
                } else {
                    throw new RuntimeException("need an argType when the target is a collection");
                }
            }
            return Conversions.convertString(env.request.getParam(name), targetType);
        }
    }

    public static class Header extends ChameleonArgument {

        private final String headerName;

        public Header(String headerName) {
            this(headerName, null);
        }

        public Header(String headerName, Class<?> collectionType) {
            type = Collection.class;
            typeArg = collectionType;
            this.headerName = headerName;
        }

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            if (targetType.isAssignableFrom(Collection.class)) {
                if (typeArg != null) {
                    return Conversions.convertCollection(env.request.getHeaders(headerName), typeArg);
                } else {
                    throw new RuntimeException("need an argType when the target is a collection");
                }
            }
            return Conversions.convertString(env.request.getHeader(headerName), targetType);
        }
    }

    public static class Headers implements ActionArgument {

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            return env.request.getHeaders();
        }
    }

    public static class RequestParameters implements ActionArgument {

        @Override
        public Object resolve(Environment env, Class<?> targetType) {
            return env.request.getParams();
        }
    }

    public static abstract class ChameleonArgument implements ActionArgument {
        protected Class<?> type;
        protected Class<?> typeArg;

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

        public final <T> Collection<T> asCollection(Class<T> clazz) {
            this.type = Collection.class;
            this.typeArg = clazz;
            // TODO homemade collection implementation with unsupported operation ?
            return Collections.emptyList();
        }
    }

    Object resolve(Environment env, Class<?> targetType);

}
