package vinna.samples.todo.controller;

import vinna.Validation;
import vinna.response.Redirect;
import vinna.response.Response;
import vinna.response.StringResponse;
import vinna.samples.todo.model.Todo;
import vinna.samples.todo.model.TodoRepository;
import vinna.samples.todo.view.CreateView;
import vinna.samples.todo.view.ListView;

public class TodoController {

    public Response list() {
        return new ListView(TodoRepository.findAll());
    }

    public Response create(Todo todo) {
        return new StringResponse(todo.toString());
    }

    public Response create(String title, String description) {
        Validation validation = new Validation();
        validation.required(title, "title").required(description, "description");

        if (validation.hasErrors()) {
            return new CreateView(title, description, validation);
        }

        Todo newTodo = new Todo();
        newTodo.setId(TodoRepository.incrementAndGetId());
        newTodo.setTitle(title);
        newTodo.setDescription(description);
        TodoRepository.putTodo(newTodo.getId(), newTodo);
        return Redirect.found("");
    }

    public Response create() {
        return new CreateView();
    }
}
