package vinna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.VuntimeException;
import vinna.outcome.Outcome;
import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;
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

        String appClass = (String) cfg.get(APPLICATION_CLASS);
        if (appClass != null) {
            try {
                Class<Vinna> clz = (Class<Vinna>) Class.forName(filterConfig.getInitParameter(APPLICATION_CLASS));
                Constructor<Vinna> cons = clz.getDeclaredConstructor(Map.class);
                vinna = cons.newInstance(cfg);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                throw new ServletException(e);
            } catch (NoSuchMethodException | InvocationTargetException e) {
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

            RouteResolution resolvedRoute = vinna.match(vinnaRequest);
            if (resolvedRoute != null) {
                try {
                    Outcome outcome = resolvedRoute.callAction(vinna);
                    outcome.execute(vinnaRequest, vinnaResponse);
                } catch (VuntimeException e) {
                    logger.error("Error while processing the request", e);
                    e.printStackTrace(vinnaResponse.getWriter());
                    vinnaResponse.setStatus(500);
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
