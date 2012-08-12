package vinna.samples.todo;

import vinna.Vinna;
import vinna.samples.todo.controller.TodoController;

public class TodoApp extends Vinna {

    @Override
    protected void routes() {
        get("/").withController(TodoController.class).list();
        get("/{id: \\d+}").withController(TodoController.class).getById(param("id").asLong());
        post("/new").withController(TodoController.class).create(req.param("title").asString(), req.param("description").asString());
    }
}
