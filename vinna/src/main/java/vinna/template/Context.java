package vinna.template;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Context {
    private static final Object NOT_FOUND = new Object();
    public static final Class[] NO_ARGS = new Class[]{};
    private final Context parent;
    private final Object data;
    private final Object helper;

    public Context(Context parent, Object root) {
        this.parent = parent;
        this.data = root;
        this.helper = parent == null ? null : parent.helper;
    }

    public Context(Context parent, Object root, Object helper) {
        this.parent = parent;
        this.data = root;
        this.helper = helper;
    }

    public Object resolve(String key) {
        if (".".equals(key)) {
            return data;
        } else if (key.charAt(0) == '\'' && key.endsWith("'")) {
            Object res = internalResolve(data, key.substring(1, key.length() - 2));
            if (res == NOT_FOUND) {
                if (parent != null) {
                    return parent.resolve(key);
                } else {
                    return null;
                }
            } else {
                return res;
            }
        } else {
            String[] parts = key.split("\\.");
            Object base = data;
            for (String part : parts) {
                base = internalResolve(base, part);
                if (base == NOT_FOUND) {
                    if (parent != null) {
                        return parent.resolve(key);
                    } else {
                        return null;
                    }
                }
            }
            return base;
        }
    }

    private Object internalResolve(Object data, String key) {
        if (data == null) {
            throw new NullPointerException("Trying to access the property " + key + " on a null object");
        }
        if (data instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) data;
            if (m.containsKey(key)) {
                return m.get(key);
            }
        } else if (data != null) {
            final Accessor accessor = accessorFor(data, key, helper);
            if (accessor != null) {
                return accessor.get(data, helper);
            }
        }

        return NOT_FOUND;
    }

    private static class Key implements Serializable {
        public final String name;
        public final Class<?> clazz;

        private Key(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!clazz.equals(key.clazz)) return false;
            if (!name.equals(key.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + clazz.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return clazz + "#" + name;
        }
    }

    //FIXME: not thread safe
    private static Map<Key, Accessor> accessorCache = new HashMap<>();

    private Accessor accessorFor(Object root, String key, Object helper) {
        Key k = new Key(key, root.getClass());
        if (accessorCache.containsKey(k)) {
            return accessorCache.get(k);
        } else {
            Accessor accessor = internalAcessorFor(key, root, false);
            if (accessor == null) {
                if (helper != null) {
                    accessor = internalAcessorFor(key, helper, true);
                    if (accessor == null) {
                        Method helperMethod = searchMethod(key, helper.getClass(), new Class[]{root.getClass()}, true);
                        if (helperMethod != null) {
                            accessor = new Accessor.HelperAccessor(helperMethod);
                        }
                    }
                }
            }

            if (accessor == null) {
                accessor = Accessor.NoAccessor.INSTANCE;
            }
            accessorCache.put(k, accessor);

            return accessor;
        }
    }

    private Accessor internalAcessorFor(String key, Object root, boolean helperMode) {
        Method getter = searchMethod("get" + capitalize(key), root.getClass(), NO_ARGS, true);
        if (getter == null) {
            getter = searchMethod("is" + capitalize(key), root.getClass(), NO_ARGS, true);
        }
        if (getter != null) {
            return new Accessor.MethodAccessor(getter, helperMode);
        }

        Method method = searchMethod(key, root.getClass(), NO_ARGS, true);
        if (method != null) {
            method.setAccessible(true);
            return new Accessor.MethodAccessor(method, helperMode);
        }

        try {
            Field field = root.getClass().getField(key);
            return new Accessor.FieldAccessor(field, helperMode);
        } catch (NoSuchFieldException e) {
            //nop
        }
        return null;
    }

    private static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    private static Method searchMethod(String methodName, Class clazz, Class[] args, boolean notVoid) {
        for (Class base = clazz; base != null; base = base.getSuperclass()) {
            Method[] methods = base.getDeclaredMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName()) && method.getParameterTypes().length == args.length && Modifier.isPublic(
                        method.getModifiers())) {
                    boolean match = true;
                    for (int i = 0; i < args.length; i++) {
                        Class argClass = args[i];
                        if (!method.getParameterTypes()[i].isAssignableFrom(argClass)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        if (notVoid) {
                            if (!method.getReturnType().equals(Void.TYPE) && !method.getReturnType().equals(Void.class)) {
                                return method;
                            }
                        } else {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }

    private interface Accessor {
        Object get(Object root, Object helper);

        public static class MethodAccessor implements Accessor {
            private final Method getter;
            private final boolean helperMode;

            public MethodAccessor(Method getter, boolean helperMode) {
                this.getter = getter;
                this.helperMode = helperMode;
            }

            @Override
            public Object get(Object root, Object helper) {
                try {
                    return getter.invoke(helperMode ? helper : root);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static class FieldAccessor implements Accessor {
            private final Field getter;
            private final boolean helperMode;

            public FieldAccessor(Field getter, boolean helperMode) {
                this.getter = getter;
                this.helperMode = helperMode;
            }

            @Override
            public Object get(Object root, Object helper) {
                try {
                    return getter.get(helperMode ? helper : root);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static class HelperAccessor implements Accessor {
            private final Method method;

            public HelperAccessor(Method method) {
                this.method = method;
            }

            @Override
            public Object get(Object root, Object helper) {
                try {
                    return method.invoke(helper, root);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static class NoAccessor implements Accessor {
            public static final NoAccessor INSTANCE = new NoAccessor();

            @Override
            public Object get(Object root, Object helper) {
                return NOT_FOUND;
            }
        }
    }
}
