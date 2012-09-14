package vinna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.InternalVinnaException;
import vinna.exception.PassException;
import vinna.exception.VuntimeException;
import vinna.http.VinnaMultipartWrapper;
import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.interceptor.Interceptor;
import vinna.response.Response;
import vinna.route.RouteResolution;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class VinnaFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(VinnaFilter.class);

    public static final String APPLICATION_CLASS = "application-class";
    public static final String VINNA_SESSION_KEY = "vinna.session";

    private Vinna vinna;
    protected ServletContext servletContext;
    private List<Interceptor> interceptors = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        Map<String, Object> cfg = new HashMap<>();

        final Object tempDir = filterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
        if (tempDir != null) {
            cfg.put(Vinna.UPLOAD_DIR, ((File) tempDir).getAbsolutePath());
        }

        final Enumeration initParameterNames = filterConfig.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            final String name = (String) initParameterNames.nextElement();
            cfg.put(name, filterConfig.getInitParameter(name));
        }

        if (cfg.get(APPLICATION_CLASS) != null) {
            String appClass = (String) cfg.get(APPLICATION_CLASS);
            vinna = createUserVinnaApp(appClass, cfg);
        } else {
            vinna = new Vinna();
        }

        vinna.init(cfg);
        this.interceptors.addAll(vinna.getInterceptors());
    }

    protected Vinna createUserVinnaApp(String appClass, Map<String, Object> cfg) throws ServletException {
        try {
            Class<Vinna> clz = (Class<Vinna>) Thread.currentThread().getContextClassLoader().loadClass(appClass);
            return clz.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            VinnaRequestWrapper vinnaRequest;
            if (isMultipartContent((HttpServletRequest) request)) {
                vinnaRequest = new VinnaMultipartWrapper((HttpServletRequest) request, (File) vinna.getConfig().get(Vinna.UPLOAD_DIR), (Integer) vinna.getConfig().get(Vinna.UPLOAD_MAX_SIZE));
            } else {
                vinnaRequest = new VinnaRequestWrapper((HttpServletRequest) request);
            }
            VinnaResponseWrapper vinnaResponse = new VinnaResponseWrapper((HttpServletResponse) response);

            HttpSession httpSession = vinnaRequest.getSession(false);
            Session session;
            if (httpSession != null && httpSession.getAttribute(VINNA_SESSION_KEY) != null) {
                session = (Session) httpSession.getAttribute(VINNA_SESSION_KEY);
            } else {
                session = vinna.newSession();
            }
            VinnaContext.set(new VinnaContext(vinna, vinnaRequest, vinnaResponse, servletContext, session));

            logger.debug("Resolving '{} {}'", vinnaRequest.getMethod(), vinnaRequest.getPath());

            callBeforeMatchInterceptors(vinnaRequest);
            RouteResolution resolvedRoute = vinna.getRouter().match(vinnaRequest);
            Response routeResponse = callAfterMatchInterceptors(vinnaRequest, resolvedRoute != null);

            if (resolvedRoute != null && routeResponse == null) {
                routeResponse = resolvedRoute.callAction(vinna);
            }

            if (routeResponse != null) {
                try {
                    callBeforeExecute(vinnaRequest, vinnaResponse);
                    routeResponse.execute(vinnaRequest, vinnaResponse);
                    // TODO call after execute interceptor

                    httpSession = vinnaRequest.getSession(false);
                    if (httpSession != null) {
                        httpSession.setAttribute(VINNA_SESSION_KEY, session);
                    }
                } catch (VuntimeException e) {
                    // TODO call after execute interceptor
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
                logger.debug("Unable to resolve '{} {}'", vinnaRequest.getMethod(), vinnaRequest.getPath());
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void callBeforeMatchInterceptors(VinnaRequestWrapper request) {
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeMatch(request);
        }
    }

    private void callBeforeExecute(VinnaRequestWrapper request, VinnaResponseWrapper response) {
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeExecute(request, response);
        }
    }

    private void callAfterExecute(VinnaRequestWrapper request, VinnaResponseWrapper response) {
        for (Interceptor interceptor : interceptors) {
            interceptor.afterExecute(request, response);
        }
    }

    // TODO defined how to interact with route resolution
    private Response callAfterMatchInterceptors(VinnaRequestWrapper request, boolean hasMatched) {
        Response interceptorResponse;
        for (Interceptor interceptor : interceptors) {
            interceptorResponse = interceptor.afterMatch(request, hasMatched);
            if (interceptorResponse != null) {
                return interceptorResponse;
            }
        }
        return null;
    }

    private boolean isMultipartContent(HttpServletRequest request) {
        if ("GET".equals(request.getMethod().toUpperCase())) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    @Override
    public void destroy() {
    }
}
