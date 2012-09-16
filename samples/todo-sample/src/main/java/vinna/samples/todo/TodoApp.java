package vinna.samples.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vinna.Vinna;
import vinna.http.VinnaRequestWrapper;
import vinna.http.VinnaResponseWrapper;
import vinna.interceptor.InterceptorAdapter;
import vinna.response.Response;
import vinna.response.ResponseBuilder;
import vinna.response.StringResponse;
import vinna.samples.todo.controller.TodoController;

import java.util.Map;

public class TodoApp extends Vinna {
    private static final Logger logger = LoggerFactory.getLogger(TodoApp.class);

    @Override
    protected void routes(Map<String, Object> config) {
        get("/").withController(TodoController.class).list();
        get("/new").withController(TodoController.class).create();
        post("/new").withController(TodoController.class).create(req.param("title").asString(), req.param("description").asString());
    }

    @Override
    protected void registerCallback(Map<String, Object> config) {

        registerInterceptor(new InterceptorAdapter() {
            @Override
            public void beforeMatch(VinnaRequestWrapper request, VinnaResponseWrapper response) {
                request.setAttribute("currentTime", System.currentTimeMillis());
            }

            @Override
            public Response afterMatch(VinnaRequestWrapper request, VinnaResponseWrapper response, boolean hasMatched) {
                logger.debug("Route matching: {} ms", (System.currentTimeMillis() - (Long) request.getAttribute("currentTime")));
                return null;
            }

            @Override
            public void beforeExecute(VinnaRequestWrapper request, VinnaResponseWrapper response) {
                request.setAttribute("currentTime", System.currentTimeMillis());
            }

            @Override
            public void afterExecute(VinnaRequestWrapper request, VinnaResponseWrapper response) {
                logger.debug("Executing time: {} ms", (System.currentTimeMillis() - (Long) request.getAttribute("currentTime")));
            }
        });

        registerInterceptor(new InterceptorAdapter() {
            @Override
            public Response afterMatch(VinnaRequestWrapper request, VinnaResponseWrapper response, boolean hasMatched) {
                if (!hasMatched && request.getPath().startsWith("/css")) {
                    return ResponseBuilder.pass();
                } else if (!hasMatched) {
                    return new StringResponse("Route not found");
                }
                return null;
            }
        });
    }
}
