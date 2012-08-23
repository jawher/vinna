package vinna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.InternalVinnaException;
import vinna.exception.PassException;
import vinna.exception.VuntimeException;
import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.response.Response;
import vinna.route.RouteResolution;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class VinnaFilter implements Filter {
    public static final String APPLICATION_CLASS = "application-class";
    private final static Logger logger = LoggerFactory.getLogger(VinnaFilter.class);

    private Vinna vinna;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Map<String, Object> cfg = new HashMap<>();
        final Enumeration initParameterNames = filterConfig.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            final String name = (String) initParameterNames.nextElement();
            cfg.put(name, filterConfig.getInitParameter(name));
        }

        if (cfg.get(APPLICATION_CLASS) != null) {
            String appClass = (String) cfg.get(APPLICATION_CLASS);
            try {
                Class<Vinna> clz = (Class<Vinna>) Thread.currentThread().getContextClassLoader().loadClass(appClass);
                try {
                    Constructor<Vinna> cons = clz.getDeclaredConstructor(Map.class);
                    vinna = cons.newInstance(cfg);
                } catch (NoSuchMethodException e) {
                    vinna = clz.newInstance();
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException | InvocationTargetException e) {
                throw new ServletException(e);
            }
        } else {
            vinna = new Vinna(cfg);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            VinnaRequestWrapper vinnaRequest = new VinnaRequestWrapper((HttpServletRequest) request);
            VinnaResponseWrapper vinnaResponse = new VinnaResponseWrapper((HttpServletResponse) response);

            VinnaContext.set(new VinnaContext(vinna, vinnaRequest, vinnaResponse));

            RouteResolution resolvedRoute = vinna.match(vinnaRequest);
            if (resolvedRoute != null) {
                try {
                    Response outcome = resolvedRoute.callAction(vinna);
                    outcome.execute(vinnaRequest, vinnaResponse);
                } catch (VuntimeException e) {
                    logger.error("Error while processing the request", e);
                    e.printStackTrace(vinnaResponse.getWriter());
                    vinnaResponse.setStatus(500);
                } catch (PassException e) {
                    logger.info("Response delegated to FilterChain.doChain");
                    chain.doFilter(request, response);
                } catch (InternalVinnaException e) {
                    logger.error("Vinna internal error occurred !", e);
                    throw new ServletException(e);
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
