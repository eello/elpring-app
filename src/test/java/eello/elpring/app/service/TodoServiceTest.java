package eello.elpring.app.service;

import eello.elpring.app.domain.Todo;
import eello.elpring.app.domain.TodoRequest;
import eello.elpring.app.domain.TodoResponse;
import eello.elpring.app.repository.MemoryTodoRepository;
import eello.elpring.app.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

    private TodoRepository todoRepository;
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoRepository = new MemoryTodoRepository();
        todoRepository.clear();
        todoService = new TodoService(todoRepository);
    }

    @Test
    void createTodo() {
        // given
        TodoRequest request = new TodoRequest("Service Test", false);

        // when
        TodoResponse response = todoService.create(request);

        // then
        assertNotNull(response.getId());
        assertEquals("Service Test", response.getTitle());
        assertFalse(response.isCompleted());
    }

    @Test
    void findById_Success() {
        // given
        Todo todo = new Todo(null, "Existing Todo", true);
        Todo saved = todoRepository.save(todo);

        // when
        TodoResponse response = todoService.findById(saved.getId());

        // then
        assertEquals(saved.getId(), response.getId());
        assertEquals("Existing Todo", response.getTitle());
        assertTrue(response.isCompleted());
    }

    @Test
    void findById_ThrowsException_WhenNotFound() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> todoService.findById(999L));
    }

    @Test
    void findAll_WithStatusFiltering() {
        // given
        todoRepository.save(new Todo(null, "Todo 1", true));
        todoRepository.save(new Todo(null, "Todo 2", false));
        todoRepository.save(new Todo(null, "Todo 3", true));

        // when & then
        // 1. 전체 조회
        List<TodoResponse> all = todoService.findAll(null);
        assertEquals(3, all.size());

        // 2. 완료(completed) 필터 조회
        List<TodoResponse> completed = todoService.findAll("completed");
        assertEquals(2, completed.size());
        assertTrue(completed.stream().allMatch(TodoResponse::isCompleted));

        // 3. 미완료(active/uncompleted) 필터 조회
        List<TodoResponse> active = todoService.findAll("active");
        assertEquals(1, active.size());
        assertFalse(active.get(0).isCompleted());
    }

    @Test
    void updateTodo_Success() {
        // given
        Todo todo = new Todo(null, "Old Title", false);
        Todo saved = todoRepository.save(todo);
        TodoRequest updateRequest = new TodoRequest("New Title", true);

        // when
        TodoResponse updated = todoService.update(saved.getId(), updateRequest);

        // then
        assertEquals("New Title", updated.getTitle());
        assertTrue(updated.isCompleted());
    }

    @Test
    void deleteTodo_Success() {
        // given
        Todo todo = new Todo(null, "Delete Me", false);
        Todo saved = todoRepository.save(todo);

        // when
        todoService.deleteById(saved.getId());

        // then
        assertFalse(todoRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void deleteTodo_ThrowsException_WhenNotFound() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> todoService.deleteById(999L));
    }
}
