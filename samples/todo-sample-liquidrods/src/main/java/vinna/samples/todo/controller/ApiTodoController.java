package vinna.samples.todo.controller;

import vinna.response.ClientError;
import vinna.response.Response;
import vinna.response.Success;
import vinna.samples.todo.JsonResponse;
import vinna.samples.todo.model.Todo;
import vinna.samples.todo.model.TodoRepository;

import java.util.Collection;

public class ApiTodoController {

    public Response create(Todo todo) {
        if (todo != null && todo.getDescription() != null && todo.getTitle() != null) {
            Long id = TodoRepository.addNewTodo(todo);
            return Success.created().redirect("api/" + id);
        }
        return ClientError.badRequest();
    }

    public Response list() {
        Collection<Todo> todos = TodoRepository.findAll();

        if (todos.isEmpty()) {
            return Success.noContent();
        }

        return new JsonResponse().entity(todos);
    }

    public Response show(long id) {

        Todo todo = TodoRepository.get(id);
        if (todo != null) {
            return new JsonResponse().entity(todo);
        }
        return ClientError.notFound();

    }
}
