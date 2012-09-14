package vinna.template;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Context parent;
    private final Map<String, Object> data = new HashMap<>();
    private final Object helper;

    public Context(Context parent, Object root) {
        this.parent = parent;
        this.data.put(".", root);
        this.helper = parent == null ? null : parent.helper;
    }

    public Context(Context parent, Object root, Object helper) {
        this.parent = parent;
        this.data.put(".", root);
        this.helper = helper;
    }

    public void add(String key, Object value) {
        data.put(key, value);
    }

    public Object resolve(String key) {
        if (".".equals(key)) {
            return data.get(key);
        } else if (key.matches("'.+'")) {
            Object res = internalResolve(key.substring(1, key.length() - 2));
            if (res == null) {
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
            Context context = this;
            Object res = null;
            for (String part : parts) {
                res = context.internalResolve(part);
                if (res == null) {
                    if (parent != null) {
                        return parent.resolve(key);
                    } else {
                        return null;
                    }
                } else {
                    context = new Context(context, res, helper);
                }
            }
            return res;
        }
    }

    private Object internalResolve(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            Object root = data.get(".");
            if (root instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) root;
                if (m.containsKey(key)) {
                    return m.get(key);
                }
            } else {
                try {
                    Method getter = new PropertyDescriptor(key, root.getClass()).getReadMethod();
                    if (getter != null) {
                        return getter.invoke(root);
                    }
                } catch (IntrospectionException e) {
                    // nop
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    Method method = root.getClass().getMethod(key);
                    method.setAccessible(true);
                    return method.invoke(root);
                } catch (IllegalAccessException | NoSuchMethodException e) {
                    // nop
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Field field = root.getClass().getField(key);
                    return field.get(root);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    //nop
                }

                if (helper != null) {
                    try {
                        Method[] methods = helper.getClass().getDeclaredMethods();
                        for (Method method : methods) {
                            if (key.equals(method.getName()) && method.getParameterTypes().length == 1) {
                                if (method.getParameterTypes()[0].isAssignableFrom(root.getClass())) {
                                    return method.invoke(helper, root);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        // nop
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }
}
