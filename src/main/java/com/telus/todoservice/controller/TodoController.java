package com.telus.todoservice.controller;


import com.telus.todoservice.model.Todo;
import com.telus.todoservice.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {


    public TodoController(@Autowired TodoService todoService) {
        this.todoService = todoService;
    }


    private final TodoService todoService;

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.findAllTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return todoService.findTodoById(id)
                .map(todo -> ResponseEntity.ok(todo))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.saveTodo(todo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        return todoService.findTodoById(id)
                .map(existingTodo -> {
                    existingTodo.setDescription(todo.getDescription());
                    existingTodo.setCompletionStatus(todo.getCompletionStatus());
                    Todo updatedTodo = todoService.update(existingTodo);
                    return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        if (!todoService.findTodoById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        todoService.deleteTodo(id);
        return ResponseEntity.ok().build();
    }
}
