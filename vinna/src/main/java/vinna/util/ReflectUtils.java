package vinna.util;

public class ReflectUtils {

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

}
