package vinna.samples.todo.controller;

import vinna.response.Forward;
import vinna.response.Redirect;
import vinna.response.Response;
import vinna.samples.todo.model.Todo;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class TodoController {

    private static final AtomicLong todoIdGenerator = new AtomicLong(0);
    private static final ConcurrentSkipListMap<Long, Todo> todoRepository = new ConcurrentSkipListMap<>();


    public Response list() {
        Forward forward = new Forward("/WEB-INF/index.jsp");
        forward.setAttribute("todos", todoRepository.values());
        return forward;
    }

    public Response create(String title, String description) {
        boolean hasTitleError = false;
        boolean hasDescriptionError = false;

        if (title == null || title.isEmpty()) {
            hasTitleError = true;
        }
        if (description == null || description.isEmpty()) {
            hasDescriptionError = true;
        }
        if (hasDescriptionError || hasTitleError) {
            Forward forwardResponse = new Forward("/WEB-INF/create.jsp");
            forwardResponse.setAttribute("titleError", hasTitleError);
            forwardResponse.setAttribute("title", title);
            forwardResponse.setAttribute("descriptionError", hasDescriptionError);
            forwardResponse.setAttribute("description", description);
            return forwardResponse;
        }

        Todo newTodo = new Todo();
        newTodo.setId(todoIdGenerator.incrementAndGet());
        newTodo.setTitle(title);
        newTodo.setDescription(description);
        todoRepository.put(newTodo.getId(), newTodo);

        return Redirect.found("");
    }

    public Response create() {
        return new Forward("/WEB-INF/create.jsp");
    }
}
