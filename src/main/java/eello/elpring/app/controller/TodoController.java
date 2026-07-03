package eello.elpring.app.controller;

import eello.elpring.app.domain.TodoRequest;
import eello.elpring.app.domain.TodoResponse;
import eello.elpring.app.service.TodoService;
import eello.elpring.web.bind.annotation.*;

import java.util.List;

@Controller
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/todos")
    public TodoResponse create(@RequestBody TodoRequest request) {
        return todoService.create(request);
    }

    @GetMapping("/todos")
    public List<TodoResponse> findAll(@RequestParam(required = false) String status) {
        return todoService.findAll(status);
    }

    @GetMapping("/todos/{id}")
    public TodoResponse findById(@PathVariable Long id) {
        return todoService.findById(id);
    }

    @PutMapping("/todos/{id}")
    public TodoResponse update(@PathVariable Long id, @RequestBody TodoRequest request) {
        return todoService.update(id, request);
    }

    @DeleteMapping("/todos/{id}")
    public void deleteById(@PathVariable Long id) {
        todoService.deleteById(id);
    }
}
