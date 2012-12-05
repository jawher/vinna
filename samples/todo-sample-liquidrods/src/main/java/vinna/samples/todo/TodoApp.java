package vinna.samples.todo;

import vinna.Vinna;
import vinna.samples.todo.controller.ApiTodoController;
import vinna.samples.todo.controller.TodoController;
import vinna.samples.todo.model.Todo;

import java.util.Map;

public class TodoApp extends Vinna {

    @Override
    protected void routes(Map<String, Object> config) {
        get("/css/bootstrap.min.css").pass();

        get("/").withController(TodoController.class).list();
        get("/new").withController(TodoController.class).create();
        post("/new").withController(TodoController.class).create(req.param("title").asString(), req.param("description").asString());

        get("/api").withController(ApiTodoController.class).list();
        get("/api/{id: \\d+}").withController(ApiTodoController.class).show(param("id").asLong());
        post("/api").withController(ApiTodoController.class).create(custom(new JacksonArgument()).<Todo>asT());

        get("/create").withController(TodoController.class).create(custom(new TodoParameter()).asTodo());
        get("/create/{title}/{description}").withController(TodoController.class).create(custom(new TodoParameter()).asTodo());
    }
}
