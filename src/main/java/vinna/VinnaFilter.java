package vinna;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

public class VinnaFilter implements Filter {

    private Router router;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String routesPath = filterConfig.getInitParameter("routes");
        System.out.println("routes: " + routesPath);
        router = new Router();
        try {
            router.loadFrom(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(routesPath), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServletException("shit !", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        System.out.println(r.getMethod());
        System.out.println(r.getServletPath());
        System.out.println(r.getParameterMap());
        Router.Route route = router.match(r);
        if (route != null) {
            HttpServletResponse s = (HttpServletResponse) response;
            s.setStatus(200);
            s.getOutputStream().write(("Matched "+route).getBytes("utf-8"));
        } else {
            System.out.println("No match");
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
