package eello.elpring.app.repository;

import eello.elpring.app.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    List<Todo> findAll();
    void deleteById(Long id);
    void clear(); // 테스트 편의를 위한 메서드
}
