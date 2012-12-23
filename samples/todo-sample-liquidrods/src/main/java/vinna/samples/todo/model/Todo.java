package vinna.samples.todo.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Todo {

    private Long id;

    @NotNull
    @Size(min = 2, max = 128)
    private String title;

    @NotNull
    @Size(max = 512)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Todo[" + title + ":" + description + "]";
    }
}
