package vinna;

import vinna.exception.VuntimeException;
import vinna.outcome.Outcome;
import vinna.request.VinnaRequestWrapper;
import vinna.response.VinnaResponseWrapper;
import vinna.route.RouteResolution;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
                vinna = new Vinna();
            }
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
