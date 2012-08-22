package vinna.samples.todo.controller;

import vinna.response.ForwardResponse;
import vinna.response.RedirectResponse;
import vinna.response.Response;
import vinna.samples.todo.model.Todo;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class TodoController {

    private static final AtomicLong todoIdGenerator = new AtomicLong(0);
    private static final ConcurrentSkipListMap<Long, Todo> todoRepository = new ConcurrentSkipListMap<>();


    public Response list() {
        ForwardResponse forward = new ForwardResponse("/WEB-INF/index.jsp");
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
            ForwardResponse forwardResponse = new ForwardResponse("/WEB-INF/create.jsp");
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

        return new RedirectResponse("");
    }

    public Response create() {
        return new ForwardResponse("/WEB-INF/create.jsp");
    }
}
