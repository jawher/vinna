package vinna;

import vinna.outcome.Outcome;
import vinna.route.Route;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
                vinna = new Vinna(routesPath);
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
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            Route.RouteResolution resolvedRoute = vinna.match(httpRequest);
            if (resolvedRoute != null) {
                try {
                    Outcome outcome = resolvedRoute.callAction();
                    outcome.execute(httpRequest, httpResponse);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.getCause().printStackTrace(httpResponse.getWriter());
                    httpResponse.setStatus(500);
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
