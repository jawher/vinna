package vinna.template;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Context {
    private static final Object NOT_FOUND = new Object();
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
            try {
                Method getter = new PropertyDescriptor(key, root.getClass()).getReadMethod();
                if (getter != null) {
                    final Accessor.MethodAccessor accessor = new Accessor.MethodAccessor(getter);
                    accessorCache.put(k, accessor);
                    return accessor;
                }
            } catch (IntrospectionException e) {
                // nop
            }
            try {
                Method method = root.getClass().getMethod(key);
                method.setAccessible(true);
                final Accessor.MethodAccessor accessor = new Accessor.MethodAccessor(method);
                accessorCache.put(k, accessor);
                return accessor;
            } catch (NoSuchMethodException e) {
                // nop
            }
            try {
                Field field = root.getClass().getField(key);
                final Accessor.FieldAccessor accessor = new Accessor.FieldAccessor(field);
                accessorCache.put(k, accessor);
                return accessor;
            } catch (NoSuchFieldException e) {
                //nop
            }

            if (helper != null) {
                Method[] methods = helper.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (key.equals(method.getName()) && method.getParameterTypes().length == 1) {
                        if (method.getParameterTypes()[0].isAssignableFrom(root.getClass())) {
                            final Accessor.HelperAccessor accessor = new Accessor.HelperAccessor(method);
                            accessorCache.put(k, accessor);
                            return accessor;
                        }
                    }
                }
            }

            accessorCache.put(k, Accessor.NoAccessor.INSTANCE);
            return Accessor.NoAccessor.INSTANCE;
        }
    }

    private interface Accessor {
        Object get(Object root, Object helper);

        public static class MethodAccessor implements Accessor {
            private final Method getter;

            public MethodAccessor(Method getter) {
                this.getter = getter;
            }

            @Override
            public Object get(Object root, Object helper) {
                try {
                    return getter.invoke(root);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static class FieldAccessor implements Accessor {
            private final Field getter;

            public FieldAccessor(Field getter) {
                this.getter = getter;
            }

            @Override
            public Object get(Object root, Object helper) {
                try {
                    return getter.get(root);
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