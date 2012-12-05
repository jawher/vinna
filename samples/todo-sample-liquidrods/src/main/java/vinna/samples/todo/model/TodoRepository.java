package vinna.samples.todo.model;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class TodoRepository {

    private static final AtomicLong todoIdGenerator = new AtomicLong(0);
    private static final ConcurrentSkipListMap<Long, Todo> todoRepository = new ConcurrentSkipListMap<>();

    public static Collection<Todo> findAll() {
        return Collections.unmodifiableCollection(todoRepository.values());
    }

    public static Long incrementAndGetId() {
        return todoIdGenerator.incrementAndGet();
    }

    public static void putTodo(Long id, Todo todo) {
        todoRepository.put(id, todo);
    }

    public static Long addNewTodo(Todo todo) {
        Long id = incrementAndGetId();
        todo.setId(id);
        putTodo(id, todo);
        return id;
    }

    public static Todo get(Long id) {
        return todoRepository.get(id);
    }
}
