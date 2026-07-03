package eello.elpring.app.repository;

import eello.elpring.app.domain.Todo;
import eello.elpring.di.annotation.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryTodoRepository implements TodoRepository {

    private final Map<Long, Todo> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(sequence.incrementAndGet());
        }
        store.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
        sequence.set(0L);
    }
}
