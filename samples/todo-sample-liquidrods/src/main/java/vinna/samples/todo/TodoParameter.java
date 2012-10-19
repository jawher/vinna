package vinna.samples.todo;

import vinna.route.ActionArgument;
import vinna.route.RouteResolution;
import vinna.samples.todo.model.Todo;

public class TodoParameter implements ActionArgument {

    @Override
    public Object resolve(RouteResolution.Action.Environment env, Class<?> targetType) {
        Todo todo = new Todo();

        if (env.matchedVars.containsKey("title")) {
            todo.setTitle(env.matchedVars.get("title"));
            todo.setDescription(env.matchedVars.get("description"));
        } else {
            todo.setTitle(env.request.getParameter("title"));
            todo.setDescription(env.request.getParameter("description"));
        }
        return todo;
    }

    @Override
    public boolean compatibleWith(Class<?> type) {
        return type.isAssignableFrom(Todo.class);
    }

    public Todo asTodo() {
        return null;
    }
}
