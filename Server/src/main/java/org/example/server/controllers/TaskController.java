package org.example.server.controllers;

import jakarta.validation.Valid;
import org.example.server.models.Task;
import org.example.server.models.User;
import org.example.server.services.TaskService;
import org.example.server.dto.TaskCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.example.server.repositories.UserRepository;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/task")
public class TaskController {

    public Long userIdSave;
    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(UserRepository userRepository, TaskService taskService) {
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    @PostMapping("/{userIdSave}")
    public ResponseEntity<Task> createTask(@PathVariable Long userIdSave, @RequestBody TaskCreateRequest taskCreateRequest) {
        // Assign the logged-in user's ID automatically
        Set<Long> userIds = new HashSet<>();
        userIds.add(userIdSave);
        taskCreateRequest.setUserIds(userIds);

        // Debugging logs
        System.out.println("TaskCreateRequest userIds: " + taskCreateRequest.getUserIds());

        Task createdTask = taskService.createTask(taskCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }


    @PostMapping("/{parentTaskId}/subtask")
    public ResponseEntity<Task> createSubtask(@PathVariable Long parentTaskId, @RequestBody TaskCreateRequest subtaskCreateRequest) {
        Task subtask = taskService.createSubtask(parentTaskId, subtaskCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(subtask);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable Long userId) {
        System.out.println("Fetching tasks for user ID: " + userId); // Debug log
        List<Task> tasks = taskService.getTasksByUserId(userId);
        System.out.println("Tasks fetched: " + tasks); // Debug log
        userIdSave = userId;
        System.out.println("User ID: " + userIdSave);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
