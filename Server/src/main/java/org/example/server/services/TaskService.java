package org.example.server.services;

import org.example.server.models.Phase;
import org.example.server.models.Task;
import org.example.server.models.User;
import org.example.server.dto.TaskCreateRequest;
import org.example.server.repositories.SubtaskRepository;
import org.example.server.repositories.TaskRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    // Single constructor for dependency injection
    public TaskService(TaskRepository taskRepository, UserService userService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Create a task and assign users
    public Task createTask(TaskCreateRequest taskCreateRequest) throws IllegalArgumentException {
        // Create the task
        Task task = new Task();
        task.setTitle(taskCreateRequest.getTitle());
        task.setDescription(taskCreateRequest.getDescription());
        task.setCategory(taskCreateRequest.getCategory());
        task.setPriority(taskCreateRequest.getPriority());
        task.setDeadline(taskCreateRequest.getDeadline());
        task.setPhase(Phase.NOT_STARTED); // Default phase

        System.out.println("Checkpoint after new Task reached!");

        // Assign users to the task
        Set<User> assignedUsers = new HashSet<>();
        for (Long userId : taskCreateRequest.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));
            assignedUsers.add(user);
        }
        task.setAssignedUsers(assignedUsers);

        System.out.println("Checkpoint after assigning users reached!");

        // Save and return the task
        return taskRepository.save(task);
    }

    // Other methods (update, delete, etc.) for task management
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserId(userId);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(this::updateTaskPhaseBasedOnDate);
        return tasks;
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private void updateTaskPhaseBasedOnDate(Task task) {
        if (task.getDeadline() != null) {
            LocalDate deadline = LocalDate.parse(task.getDeadline());
            if (LocalDate.now().isAfter(deadline) && task.getPhase() != Phase.COMPLETED) {
                task.setPhase(Phase.OVERDUE);
            }
        }
    }
}
