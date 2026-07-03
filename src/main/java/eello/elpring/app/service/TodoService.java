package eello.elpring.app.service;

import eello.elpring.app.domain.Todo;
import eello.elpring.app.domain.TodoRequest;
import eello.elpring.app.domain.TodoResponse;
import eello.elpring.app.repository.TodoRepository;
import eello.elpring.di.annotation.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo(null, request.getTitle(), request.isCompleted());
        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.from(savedTodo);
    }

    public TodoResponse findById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found for id: " + id));
        return TodoResponse.from(todo);
    }

    public List<TodoResponse> findAll(String status) {
        List<Todo> todos = todoRepository.findAll();
        
        if (status == null || status.isEmpty()) {
            return todos.stream()
                    .map(TodoResponse::from)
                    .collect(Collectors.toList());
        }

        boolean filterCompleted = "completed".equalsIgnoreCase(status);
        return todos.stream()
                .filter(todo -> todo.isCompleted() == filterCompleted)
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found for id: " + id));
        
        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());
        
        Todo updatedTodo = todoRepository.save(todo);
        return TodoResponse.from(updatedTodo);
    }

    public void deleteById(Long id) {
        // 존재 여부 확인 후 삭제
        todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found for id: " + id));
        todoRepository.deleteById(id);
    }
}
