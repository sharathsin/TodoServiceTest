package com.telus.todoservice.controller;

import com.google.protobuf.Empty;
import com.telus.todoservice.exception.ResourceNotFoundException;
import com.telus.todoservice.grpc.TodoList;
import com.telus.todoservice.grpc.TodoRequest;
import com.telus.todoservice.grpc.todos;
import com.telus.todoservice.model.CompletionStatus;
import com.telus.todoservice.model.Todo;
import com.telus.todoservice.service.TodoService;
import io.grpc.stub.StreamObserver;

import java.lang.module.ResolutionException;
import java.util.List;

public class TodoServiceImpl extends com.telus.todoservice.grpc.TodoServiceGrpc.TodoServiceImplBase {

    private final TodoService todoService;

    public TodoServiceImpl(TodoService todoService) {
        this.todoService = todoService;
    }

    @Override
    public void getAllTodos(Empty request, StreamObserver<TodoList> responseObserver) {
        List<Todo> todos = todoService.findAllTodos();
        responseObserver.onNext(convertJavaTodoListToProtoTodoList(todos));
        responseObserver.onCompleted();
    }

    @Override
    public void getTodoById(TodoRequest request, StreamObserver<todos> responseObserver) {
        long id = Long.parseLong(request.getId());
        Todo todo = todoService.findTodoById(id).orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        responseObserver.onNext(convertTodotoProto(todo).build());
        responseObserver.onCompleted();
    }

    @Override
    public void createTodo(todos request, StreamObserver<todos> responseObserver) {
        Todo todo = toTodo(request);
        todoService.saveTodo(todo);
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void updateTodo(todos request, StreamObserver<todos> responseObserver) {
        Todo todo = toTodo(request);
        todoService.update(todo);
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTodo(TodoRequest request, StreamObserver<Empty> responseObserver) {
        long id = Long.parseLong(request.getId());
        todoService.deleteTodo(id);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    private Todo toTodo(todos todoProto) {
        long id = Long.parseLong(todoProto.getId());
        String title = todoProto.getDescription();
        String completionStatus = todoProto.getCompletionStatus();

        Todo todo = new Todo(id, title, CompletionStatus.valueOf(completionStatus));
        return todo;
    }

    public TodoList convertJavaTodoListToProtoTodoList(List<Todo> todoList) {
        TodoList.Builder todoListBuilder = TodoList.newBuilder();

        for (Todo todo : todoList) {
            todos.Builder todoBuilder = convertTodotoProto(todo);
            todoListBuilder.addTodos(todoBuilder);
        }

        return todoListBuilder.build();
    }

    private static todos.Builder convertTodotoProto(Todo todo) {
        todos.Builder todoBuilder = todos.newBuilder();

        todoBuilder.setId(todo.getId().toString());
        todoBuilder.setDescription(todo.getDescription());
        todoBuilder.setCompletionStatus(todo.getCompletionStatus().toString());

        return todoBuilder;
    }
}