package vinna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.exception.InternalVinnaException;
import vinna.exception.PassException;
import vinna.exception.VuntimeException;
import vinna.http.VinnaMultipartWrapper;
import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.response.Response;
import vinna.route.RouteResolution;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class VinnaFilter implements Filter {
    public static final String APPLICATION_CLASS = "application-class";
    private final static Logger logger = LoggerFactory.getLogger(VinnaFilter.class);

    private Vinna vinna;
    private File uploadDirectory;
    private int uploadMaxSize;
    protected ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        Map<String, Object> cfg = new HashMap<>();
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

        String confUploadTemporaryDirectory = (String) vinna.getConfig().get("upload-directory");
        if (confUploadTemporaryDirectory != null && !confUploadTemporaryDirectory.isEmpty()) {
            uploadDirectory = new File(confUploadTemporaryDirectory).getAbsoluteFile();
        } else {
            uploadDirectory = (File) filterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
            if (uploadDirectory == null) {
                String tmpdir = System.getProperty("java.io.tmpdir");
                if (tmpdir != null) {
                    uploadDirectory = new File(tmpdir).getAbsoluteFile();
                } else {
                    logger.warn("Cannot find a temporary directory for upload.");
                }
            }
        }

        uploadMaxSize = Integer.parseInt((String) vinna.getConfig().get("upload-max-size"));
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
                vinnaRequest = new VinnaMultipartWrapper((HttpServletRequest) request, uploadDirectory, uploadMaxSize);
            } else {
                vinnaRequest = new VinnaRequestWrapper((HttpServletRequest) request);
            }
            VinnaResponseWrapper vinnaResponse = new VinnaResponseWrapper((HttpServletResponse) response);

            VinnaContext.set(new VinnaContext(vinna, vinnaRequest, vinnaResponse, servletContext));

            logger.debug("Resolving '{} {}'", vinnaRequest.getMethod(), vinnaRequest.getPath());
            RouteResolution resolvedRoute = vinna.getRouter().match(vinnaRequest);
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
                logger.debug("Unable to resolve '{} {}'", vinnaRequest.getMethod(), vinnaRequest.getPath());
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private final boolean isMultipartContent(HttpServletRequest request) {
        if ("GET".equals(request.getMethod().toUpperCase())) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase().startsWith("multipart/")) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
