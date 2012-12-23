package vinna.samples.todo.controller;

import vinna.Validation;
import vinna.response.ClientError;
import vinna.response.Response;
import vinna.response.Success;
import vinna.samples.todo.model.Todo;
import vinna.samples.todo.model.TodoRepository;
import vinna.samples.todo.view.JsonResponse;

import java.util.Collection;

public class ApiTodoController {

    public Response create(Todo todo) {
        Validation validation = new Validation().validate(todo);

        if (!validation.hasErrors()) {
            Long id = TodoRepository.addNewTodo(todo);
            return Success.created().redirect("api/" + id);
        }

        return new JsonResponse(400).entity(validation.getFirstErrors());
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
