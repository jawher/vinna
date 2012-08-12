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
        ForwardOutcome forward = new ForwardOutcome("index.jsp");
        forward.setAttribute("todos", todoRepository.values());
        return forward;
    }

    public Outcome getById(long id) {
        Todo todo = todoRepository.get(id);
        if (todo != null) {
            ForwardOutcome forward = new ForwardOutcome("view.jsp");
            forward.setAttribute("todo", todo);
            return forward;
        }
        return new ForwardOutcome("404.html");
    }

    public Outcome create(String title, String description) {
        Todo newTodo = new Todo();
        newTodo.setId(todoIdGenerator.incrementAndGet());
        newTodo.setTitle(title);
        newTodo.setDescription(description);
        todoRepository.put(newTodo.getId(), newTodo);

        return new RedirectOutcome("");
    }
}
