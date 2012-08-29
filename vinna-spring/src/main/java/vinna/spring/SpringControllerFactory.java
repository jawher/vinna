package vinna.spring;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import vinna.ControllerFactory;
import vinna.VinnaContext;

public class SpringControllerFactory implements ControllerFactory {
    @Override
    public Object create(String id, Class<?> clazz) {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(VinnaContext.get().servletContext);
        if (id == null) {
            return ctx.getBean(clazz);
        } else if (clazz == null) {
            return ctx.getBean(id);
        } else {
            return ctx.getBean(id, clazz);
        }
    }
}
