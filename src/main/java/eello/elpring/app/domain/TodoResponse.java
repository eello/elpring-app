package eello.elpring.app.domain;

public class TodoResponse {
    private Long id;
    private String title;
    private boolean completed;

    public TodoResponse() {
    }

    public TodoResponse(Long id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted());
    }

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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "TodoResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
