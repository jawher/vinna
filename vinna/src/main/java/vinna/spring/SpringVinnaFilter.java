package vinna.spring;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import vinna.Vinna;
import vinna.VinnaFilter;
import vinna.exception.ConfigException;

import javax.servlet.ServletException;
import java.util.Map;

public class SpringVinnaFilter extends VinnaFilter {

    @Override
    protected Vinna createUserVinnaApp(String appClass, Map<String, Object> cfg) throws ServletException {
        try {
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            return ctx.getBean(appClass, Vinna.class);
        } catch (NoClassDefFoundError e) {
            throw new ConfigException("spring-web is not available. For using the Spring integration you have to add it to your classpath.");
        }
    }
}
