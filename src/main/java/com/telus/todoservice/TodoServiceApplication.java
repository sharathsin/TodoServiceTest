package com.telus.todoservice;

import com.telus.todoservice.controller.TodoServiceImpl;
import com.telus.todoservice.service.TodoService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;

@SpringBootApplication
public class TodoServiceApplication   {
    private final TodoService todoService;

    public TodoServiceApplication(TodoService todoService) {
        this.todoService = todoService;
    }
    public static void main(String[] args) {

    SpringApplication.run(TodoServiceApplication.class, args);
    }


}
