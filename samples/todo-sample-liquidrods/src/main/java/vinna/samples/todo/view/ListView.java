package vinna.samples.todo.view;

import vinna.samples.todo.model.Todo;
import vinna.template.LiquidrodsView;

import java.util.Collection;

public class ListView extends LiquidrodsView {

    private final Collection<Todo> todos;

    public ListView(Collection<Todo> todos) {
        this.todos = todos;
    }

    public Collection<Todo> getTodos() {
        return todos;
    }
}
