package vinna.samples.todo;

import vinna.Vinna;
import vinna.samples.todo.controller.TodoController;

import java.util.Map;

public class TodoApp extends Vinna {

    @Override
    protected void routes(Map<String, Object> config) {
        get("/css/bootstrap.min.css").pass();

        get("/").withController(TodoController.class).list();
        get("/new").withController(TodoController.class).create();
        post("/new").withController(TodoController.class).create(req.param("title").asString(), req.param("description").asString());

        get("/create").withController(TodoController.class).create(custom(new TodoParameter()).asTodo());
        get("/create/{title}/{description}").withController(TodoController.class).create(custom(new TodoParameter()).asTodo());
    }
}
