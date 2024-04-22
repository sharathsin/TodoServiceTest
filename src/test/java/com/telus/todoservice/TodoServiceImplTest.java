package com.telus.todoservice;

import com.telus.todoservice.controller.TodoServiceImpl;
import com.telus.todoservice.grpc.*;
import com.telus.todoservice.model.Todo;
import com.telus.todoservice.model.CompletionStatus;
import com.telus.todoservice.service.TodoService;
import com.google.protobuf.Empty;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceImplTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private TodoServiceGrpc.TodoServiceBlockingStub blockingStub;

    @Mock
    private TodoService todoService;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new TodoServiceImpl(todoService))
                .build()
                .start());

        blockingStub = TodoServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
    }
    @Test
    public void getAllTodosTest() {
        List<Todo> expectedTodos = Arrays.asList(
                new Todo(1L, "Do laundry", CompletionStatus.PENDING),
                new Todo(2L, "Pay bills", CompletionStatus.COMPLETED)
        );
        when(todoService.findAllTodos()).thenReturn(expectedTodos);

        TodoList response = blockingStub.getAllTodos(Empty.getDefaultInstance());

        assertEquals(2, response.getTodosCount());
        assertEquals("Do laundry", response.getTodos(0).getDescription());
        verify(todoService).findAllTodos();
    }

    @Test
    public void getTodoByIdTest() {
        Todo expectedTodo = new Todo(1L, "Do laundry", CompletionStatus.PENDING);
        when(todoService.findTodoById(1L)).thenReturn(java.util.Optional.of(expectedTodo));

        TodoRequest request = TodoRequest.newBuilder().setId("1").build();
        todos response = blockingStub.getTodoById(request);

        assertEquals("Do laundry", response.getDescription());
        verify(todoService).findTodoById(1L);
    }
    @Test
    public void createTodoTest() {
        todos todoProto = todos.newBuilder()
                .setId("0")
                .setDescription("Go shopping")
                .setCompletionStatus("PENDING")
                .build();

        Todo todo = new Todo(null, "Go shopping", CompletionStatus.PENDING);
        when(todoService.saveTodo(any(Todo.class))).thenReturn(todo);

        todos response = blockingStub.createTodo(todoProto);

        assertEquals("Go shopping", response.getDescription());
        verify(todoService).saveTodo(any(Todo.class));
    }
    @Test
    public void updateTodoTest() {
        todos todoProto = todos.newBuilder()
                .setId("1")
                .setDescription("Go shopping")
                .setCompletionStatus("PENDING")
                .build();

        Todo updatedTodo = new Todo(1L, "Go shopping", CompletionStatus.PENDING);
        when(todoService.update(any(Todo.class))).thenReturn(updatedTodo);

        todos response = blockingStub.updateTodo(todoProto);

        assertEquals("Go shopping", response.getDescription());
        verify(todoService).update(any(Todo.class));
    }
    @Test
    public void deleteTodoTest() {
        TodoRequest request = TodoRequest.newBuilder().setId("1").build();
        doNothing().when(todoService).deleteTodo(1L);

        Empty response = blockingStub.deleteTodo(request);

        assertNotNull(response);
        verify(todoService).deleteTodo(1L);
    }



}
