package vinna.samples.todo.view;

import vinna.Validation;
import vinna.template.LiquidrodsView;

public class CreateView extends LiquidrodsView {
    private final String title;
    private final String description;

    public CreateView() {
        this(null, null, null);
    }

    public CreateView(String title, String description, Validation validation) {
        this.title = title;
        this.description = description;
        this.validation = validation;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
