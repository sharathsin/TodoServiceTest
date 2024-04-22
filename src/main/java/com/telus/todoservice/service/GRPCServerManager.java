package com.telus.todoservice.service;

import com.telus.todoservice.controller.TodoServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GRPCServerManager {

    private Server server;
    private final TodoService todoService;

    public GRPCServerManager(TodoService todoService) {
        this.todoService = todoService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws IOException {
        server = ServerBuilder
                .forPort(8081)
                .addService(new TodoServiceImpl(todoService))
                .build()
                .start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        server.shutdown();
    }
}