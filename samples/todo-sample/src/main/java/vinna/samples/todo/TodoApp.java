package vinna.samples.todo;

import vinna.Vinna;
import vinna.http.VinnaRequestWrapper;
import vinna.interceptor.InterceptorAdapter;
import vinna.response.Response;
import vinna.response.StringResponse;
import vinna.samples.todo.controller.TodoController;

import java.util.Map;

public class TodoApp extends Vinna {

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
            public Response afterMatch(VinnaRequestWrapper request, boolean hasMatched) {
                if (!hasMatched) {
                    return new StringResponse("Route not found");
                }
                return null;
            }
        });
    }
}
