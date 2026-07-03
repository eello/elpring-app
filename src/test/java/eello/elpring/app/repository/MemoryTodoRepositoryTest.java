package eello.elpring.app.repository;

import eello.elpring.app.domain.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemoryTodoRepositoryTest {

    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository = new MemoryTodoRepository();
        todoRepository.clear();
    }

    @Test
    void saveAndFindById() {
        // given
        Todo todo = new Todo(null, "Test Todo", false);

        // when
        Todo savedTodo = todoRepository.save(todo);
        Optional<Todo> foundTodoOpt = todoRepository.findById(savedTodo.getId());

        // then
        assertTrue(foundTodoOpt.isPresent());
        Todo foundTodo = foundTodoOpt.get();
        assertEquals(savedTodo.getId(), foundTodo.getId());
        assertEquals("Test Todo", foundTodo.getTitle());
        assertFalse(foundTodo.isCompleted());
    }

    @Test
    void findAll() {
        // given
        Todo todo1 = new Todo(null, "Test Todo 1", false);
        Todo todo2 = new Todo(null, "Test Todo 2", true);
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        // when
        List<Todo> allTodos = todoRepository.findAll();

        // then
        assertEquals(2, allTodos.size());
    }

    @Test
    void deleteById() {
        // given
        Todo todo = new Todo(null, "Test Todo", false);
        Todo savedTodo = todoRepository.save(todo);

        // when
        todoRepository.deleteById(savedTodo.getId());
        Optional<Todo> foundTodo = todoRepository.findById(savedTodo.getId());

        // then
        assertFalse(foundTodo.isPresent());
    }
}
