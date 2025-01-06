package org.example.server.controllers;

import org.example.server.models.Phase;
import org.example.server.models.Task;
import org.example.server.services.TaskService;
import org.example.server.dto.TaskCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.server.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> taskData) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Update phase if provided
        if (taskData.containsKey("phase")) {
            String newPhase = (String) taskData.get("phase");
            try {
                Phase phase = Phase.valueOf(newPhase); // Convert string to enum
                task.setPhase(phase);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        // Update other task fields if provided
        if (taskData.containsKey("title")) {
            task.setTitle((String) taskData.get("title"));
        }
        if (taskData.containsKey("description")) {
            task.setDescription((String) taskData.get("description"));
        }
        if (taskData.containsKey("category")) {
            task.setCategory((String) taskData.get("category"));
        }
        if (taskData.containsKey("priority")) {
            task.setPriority((String) taskData.get("priority"));
        }
        if (taskData.containsKey("deadline")) {
            task.setDeadline((String) taskData.get("deadline"));
        }

        // Save the updated task
        Task updatedTask = taskService.saveTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
