package vinna.spring;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import vinna.ControllerFactory;
import vinna.VinnaContext;
import vinna.exception.ConfigException;

public class SpringControllerFactory implements ControllerFactory {

    @Override
    public Object create(String id, Class<?> clazz) {
        try {
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(VinnaContext.get().servletContext);
            if (id == null) {
                return ctx.getBean(clazz);
            } else if (clazz == null) {
                return ctx.getBean(id);
            } else {
                return ctx.getBean(id, clazz);
            }
        } catch (NoClassDefFoundError e) {
            throw new ConfigException("spring-web is not available. For using the Spring integration you have to add it to your classpath.");
        }
    }
}
