package vinna.spring;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import vinna.Vinna;
import vinna.VinnaFilter;

import javax.servlet.ServletException;
import java.util.Map;

public class SpringVinnaFilter extends VinnaFilter {
    @Override
    protected Vinna createUserVinnaApp(String appClass, Map<String, Object> cfg) throws ServletException {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        return ctx.getBean(appClass, Vinna.class);
    }
}
