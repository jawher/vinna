package vinna;

import vinna.outcome.Outcome;
import vinna.request.Request;
import vinna.request.ServletRequestWrapper;
import vinna.response.Response;
import vinna.response.ServletResponseWrapper;
import vinna.route.RouteResolution;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

public class VinnaFilter implements Filter {

    private Vinna vinna;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO
        String routesPath = filterConfig.getInitParameter("routes");
        if (routesPath != null) {
            try {
                vinna = new Vinna(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(routesPath), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new ServletException("shit !", e);
            }
        } else {
            try {
                vinna = (Vinna) Class.forName(filterConfig.getInitParameter("application-class")).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                throw new ServletException(e);
            } catch (NullPointerException e) {
                // TODO load default configuration
                vinna = new Vinna();
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            Request vinnaRequest = new ServletRequestWrapper((HttpServletRequest) request);
            Response vinnaResponse = new ServletResponseWrapper((HttpServletResponse) response);

            RouteResolution resolvedRoute = vinna.match(vinnaRequest);
            if (resolvedRoute != null) {
                try {
                    Outcome outcome = resolvedRoute.callAction(vinna);
                    outcome.execute(vinnaRequest, vinnaResponse);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.getCause().printStackTrace(vinnaResponse.getWriter());
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
