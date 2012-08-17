package vinna.samples.todo.controller;

import vinna.outcome.ForwardOutcome;
import vinna.outcome.Outcome;
import vinna.outcome.RedirectOutcome;
import vinna.samples.todo.model.Todo;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class TodoController {

    private static final AtomicLong todoIdGenerator = new AtomicLong(0);
    private static final ConcurrentSkipListMap<Long, Todo> todoRepository = new ConcurrentSkipListMap<Long, Todo>();


    public Outcome list() {
        ForwardOutcome forward = new ForwardOutcome("/WEB-INF/index.jsp");
        forward.setAttribute("todos", todoRepository.values());
        return forward;
    }

    public Outcome create(String title, String description) {
        boolean hasTitleError = false;
        boolean hasDescriptionError = false;

        if (title == null || title.isEmpty()) {
            hasTitleError = true;
        }
        if (description == null || description.isEmpty()) {
            hasDescriptionError = true;
        }
        if (hasDescriptionError || hasTitleError) {
            ForwardOutcome forwardOutcome = new ForwardOutcome("/WEB-INF/create.jsp");
            forwardOutcome.setAttribute("titleError", hasTitleError);
            forwardOutcome.setAttribute("title", title);
            forwardOutcome.setAttribute("descriptionError", hasDescriptionError);
            forwardOutcome.setAttribute("description", description);
            return forwardOutcome;
        }

        Todo newTodo = new Todo();
        newTodo.setId(todoIdGenerator.incrementAndGet());
        newTodo.setTitle(title);
        newTodo.setDescription(description);
        todoRepository.put(newTodo.getId(), newTodo);

        return new RedirectOutcome("");
    }

    public Outcome create() {
        return new ForwardOutcome("/WEB-INF/create.jsp");
    }
}
