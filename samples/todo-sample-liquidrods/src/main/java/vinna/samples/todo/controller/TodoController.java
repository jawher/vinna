package vinna.samples.todo.controller;

import vinna.Validation;
import vinna.response.Redirect;
import vinna.response.Response;
import vinna.samples.todo.model.Todo;
import vinna.samples.todo.view.CreateView;
import vinna.samples.todo.view.ListView;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class TodoController {

    private static final AtomicLong todoIdGenerator = new AtomicLong(0);
    private static final ConcurrentSkipListMap<Long, Todo> todoRepository = new ConcurrentSkipListMap<>();

    public Response list() {
        return new ListView(todoRepository.values());
    }

    public Response create(String title, String description) {
        Validation validation = new Validation();
        validation.required(title, "title").required(description, "description");

        if (validation.hasErrors()) {
            return new CreateView(title, description, validation);
        }

        Todo newTodo = new Todo();
        newTodo.setId(todoIdGenerator.incrementAndGet());
        newTodo.setTitle(title);
        newTodo.setDescription(description);
        todoRepository.put(newTodo.getId(), newTodo);
        return Redirect.found("");
    }

    public Response create() {
        return new CreateView();
    }
}
