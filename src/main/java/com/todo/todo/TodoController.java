package com.todo.todo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final List<Todo> todoList = new ArrayList<>();

     // GET all todos
    @GetMapping("/")
    public ResponseEntity<List<Todo>> getAllTodos(@RequestParam(defaultValue = "true") Boolean isCompleted) {
        List<Todo> filteredTodos = todoList.stream()
            .filter(todo -> todo.isCompleted()==isCompleted)
            .collect(Collectors.toList());

        if (filteredTodos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(filteredTodos);
    }

    // GET todo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable int id) {
        return todoList.stream()
                .filter(todo -> todo.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST a new todo
    @PostMapping("/")
    public ResponseEntity<Todo> addTodo(@RequestBody Todo newTodo) {
        todoList.add(newTodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> updateTodoPartially(
        @PathVariable int id,
        @RequestBody Map<String, Object> updates){

            Optional<Todo> optionalTodo = todoList.stream()
            .filter(todo -> todo.getId() == id)
            .findFirst();

            if (optionalTodo.isPresent()) {
                Todo todo = optionalTodo.get();
        
                // Only update the 'completed' field if it is present in the request
                if (updates.containsKey("completed")) {
                    todo.setCompleted((Boolean) updates.get("completed"));
                }

                if(updates.containsKey("title")){
                    todo.setTitle((String) updates.get("title"));
                }
        
                return ResponseEntity.ok(todo);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    
        //Delete a todo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable int id) {
        Optional<Todo> optionalTodo = todoList.stream()
                .filter(todo -> todo.getId() == id)
                .findFirst();

        if (optionalTodo.isPresent()) {
            todoList.remove(optionalTodo.get());
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
