package vinna;

public class DefaultControllerFactory implements ControllerFactory {
    @Override
    public Object create(String id, Class<?> clazz) {
        if (clazz == null) {
            try {
                //TODO: handle convention based class name resolution (basePackage + ".controllers" + id + "Controller)
                clazz = Class.forName(id);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Invalid object id " + id);
            }
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Can't createController an instance of " + clazz, e);
        }
    }
}
