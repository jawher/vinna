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
            VinnaContext vinnaContext = new VinnaContext(vinna, vinnaRequest, vinnaResponse, servletContext, session);
            VinnaContext.set(vinnaContext);

            logger.debug("Resolving '{} {}'", vinnaRequest.getMethod(), vinnaRequest.getPath());

            try {
                callBeforeMatchInterceptors(vinnaContext);

                vinnaContext.routeResolution = vinna.getRouter().match(vinnaRequest);

                callAfterMatchInterceptors(vinnaContext);

                if (vinnaContext.isResolved()) {
                    vinnaContext.canAbort(false);

                    Response routeResponse = vinnaContext.routeResolution.callAction(vinna);
                    routeResponse.execute(vinnaRequest, vinnaResponse);

                    httpSession = vinnaRequest.getSession(false);
                    if (httpSession != null) {
                        httpSession.setAttribute(VINNA_SESSION_KEY, session);
                    }

                    callAfterExecute(vinnaContext);

                } else {
                    logger.debug("Unable to resolve '{} {}'", vinnaRequest.getMethod(), vinnaRequest.getPath());
                    chain.doFilter(request, response);
                }

            } catch (PassException e) {
                logger.info("Response delegated to FilterChain.doChain");
                chain.doFilter(request, response);
            } catch (VuntimeException e) {
                logger.error("Error while processing the request", e);
                vinnaResponse.setStatus(500);
                e.printStackTrace(vinnaResponse.getWriter());
            } catch (InternalVinnaException e) {
                logger.error("Vinna internal error occurred !", e);
                throw new ServletException(e);
            }

        } else {
            chain.doFilter(request, response);
        }
    }

    private void callBeforeMatchInterceptors(VinnaContext context) throws IOException, ServletException {
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeMatch(context);
            if (context.isAborted()) {
                context.sendResponse();
            }
        }
    }

    private void callAfterMatchInterceptors(VinnaContext context) throws IOException, ServletException {
        for (Interceptor interceptor : interceptors) {
            interceptor.afterMatch(context);
            if (context.isAborted()) {
                context.sendResponse();
            }
        }
    }

    private void callAfterExecute(VinnaContext context) {
        for (Interceptor interceptor : interceptors) {
            interceptor.afterExecute(context);
        }
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
