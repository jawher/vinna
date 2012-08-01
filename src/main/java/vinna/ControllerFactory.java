package vinna;

public interface ControllerFactory {
    Object create(String id, Class<?> clazz);
}
